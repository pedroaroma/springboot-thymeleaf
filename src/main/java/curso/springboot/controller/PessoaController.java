package curso.springboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private TelefoneRepository telefoneRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		// passa um objeto vazio para habilitar uma edição futura
		modelAndView.addObject("pessoaobj", new Pessoa());

		Iterable<Pessoa> pessoasit = pessoaRepository.findAll();
		// lista de pessoas para retorno
		modelAndView.addObject("pessoas", pessoasit);

		return modelAndView;
	}

	// intercepta QUALQUER coisa que esteja atrás do /salvarpessoa
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) { //anotação @Valid para realizar as validações

		if (bindingResult.hasErrors()) { //verifica se há erros na hora de validar os campos (regras das anotações no Model Pessoa)

			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");

			Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
			modelAndView.addObject("pessoas", pessoasIt);
			modelAndView.addObject("pessoaobj", pessoa);
			
			List<String> msg = new ArrayList<String>(); //Prepara o arraylist de string para armazenas as mensagens de erro
			
			for (ObjectError objectError : bindingResult.getAllErrors()) { //recupera todas as mensagens de erro das validações
				
				msg.add(objectError.getDefaultMessage()); //adiciona a lista as mensagens vindas das anotações
				
			}
			
			modelAndView.addObject("msg", msg); //joga a lista de erros para a view

			return modelAndView;

		} else {

			// salva a pessoa
			pessoaRepository.save(pessoa);

			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
			// carrega todas as pessoas cadastradas
			Iterable<Pessoa> pessoasit = pessoaRepository.findAll();
			// adiciona a lista de pessoas para ser exibida na view
			modelAndView.addObject("pessoas", pessoasit);
			// passa um objeto vazio para habilitar uma edição futura
			modelAndView.addObject("pessoaobj", new Pessoa());

			return modelAndView;
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {

		// exibe a view
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		// carrega a lista de pessoas
		Iterable<Pessoa> pessoasit = pessoaRepository.findAll();
		// objeto de retorno para a view
		modelAndView.addObject("pessoas", pessoasit);
		modelAndView.addObject("pessoaobj", new Pessoa());

		return modelAndView;
	}

	// intercepta a URL passando como parametro o id da pessoa
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {

		// carrega a pessoa pelo id
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		// prepara o retorno para a mesma tela
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		// passa o obj para edição
		modelAndView.addObject("pessoaobj", pessoa.get());
		// retorna o obj para a view
		return modelAndView;
	}

	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {

		pessoaRepository.deleteById(idpessoa);

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll());
		modelAndView.addObject("pessoaobj", new Pessoa());

		return modelAndView;
	}

	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa) {

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findPessoaByName(nomepesquisa));
		modelAndView.addObject("pessoaobj", new Pessoa());

		return modelAndView;
	}

	// Telefones
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {

		// carrega a pessoa pelo id
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		// prepara o retorno para a mesma tela
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		// passa o obj para edição
		modelAndView.addObject("pessoaobj", pessoa.get());
		// retorna o obj para a view
		// retorna a lista de telefones cadastrados do usuario
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		return modelAndView;

	}

	@PostMapping("**/addfonePessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {

		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		telefone.setPessoa(pessoa);

		telefoneRepository.save(telefone);

		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa);

		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));

		return modelAndView;
	}

	@GetMapping("/removertelefone/{idtelefone}")
	public ModelAndView excluirtelefone(@PathVariable("idtelefone") Long idtelefone) {

		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
		telefoneRepository.deleteById(idtelefone);

		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");

		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		modelAndView.addObject("pessoaobj", pessoa);

		return modelAndView;
	}

}
