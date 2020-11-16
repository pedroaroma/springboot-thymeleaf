package curso.springboot.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.repository.PessoaRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		// passa um objeto vazio para habilitar uma edição futura
		modelAndView.addObject("pessoaobj", new Pessoa());

		return modelAndView;
	}

	// intercepta QUALQUER coisa que esteja atrás do /salvarpessoa
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	public ModelAndView salvar(Pessoa pessoa) {

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

}
