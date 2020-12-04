package curso.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Profissao;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.ProfissaoRepository;
import curso.springboot.repository.TelefoneRepository;
import curso.springboot.service.ReportUtil;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ReportUtil reportUtil;
	
	@Autowired
	private ProfissaoRepository profissaoRepository;


	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		// passa um objeto vazio para habilitar uma edição futura
		modelAndView.addObject("pessoaobj", new Pessoa());

		Iterable<Pessoa> pessoasit = pessoaRepository.findAll();
		//Iterable<Pessoa> pessoasit = pessoaRepository.findAllAsc(); utilizar para exibir as pessoas cadastradas em ordem crescente pelo id
		// lista de pessoas para retorno
		modelAndView.addObject("pessoas", pessoasit);
		
		modelAndView.addObject("profissoes", profissaoRepository.findAll());

		return modelAndView;
	}

	// intercepta QUALQUER coisa que esteja atrás do /salvarpessoa
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa", consumes = {"multipart/form-data"})
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult, final MultipartFile file) throws IOException { // anotação @Valid para realizar as validações

		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		if (bindingResult.hasErrors()) { // verifica se há erros na hora de validar os campos (regras das anotações no Model Pessoa)

			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");

			Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
			modelAndView.addObject("pessoas", pessoasIt);
			modelAndView.addObject("pessoaobj", pessoa);

			List<String> msg = new ArrayList<String>(); // Prepara o arraylist de string para armazenas as mensagens de erro

			for (ObjectError objectError : bindingResult.getAllErrors()) { // recupera todas as mensagens de erro das validações

				msg.add(objectError.getDefaultMessage()); // adiciona a lista as mensagens vindas das anotações

			}
			
			
			modelAndView.addObject("profissoes", profissaoRepository.findAll());

			modelAndView.addObject("msg", msg); // joga a lista de erros para a view

			return modelAndView;

		} else {
			
			if(file.getSize() > 0) { //cadastra um novo curriculo
				
				pessoa.setCurriculo(file.getBytes());
				pessoa.setTipoFileCurriculo(file.getContentType());
				pessoa.setNomeFileCurriculo(file.getOriginalFilename());
				
			}else {
				if(pessoa.getId() != null && pessoa.getId() > 0) { //editando
					
					Pessoa pessoaTemp = pessoaRepository.findById(pessoa.getId()).get();
					pessoa.setCurriculo(pessoaTemp.getCurriculo());
					pessoa.setTipoFileCurriculo(pessoaTemp.getTipoFileCurriculo());
					pessoa.setNomeFileCurriculo(pessoaTemp.getNomeFileCurriculo());
					
				}
			}

			// salva a pessoa
			pessoaRepository.save(pessoa);

			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
			// carrega todas as pessoas cadastradas
			Iterable<Pessoa> pessoasit = pessoaRepository.findAll();
			// adiciona a lista de pessoas para ser exibida na view
			modelAndView.addObject("pessoas", pessoasit);
			// passa um objeto vazio para habilitar uma edição futura
			modelAndView.addObject("pessoaobj", new Pessoa());
			
			modelAndView.addObject("msg", "Sucesso!");

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
		
		Iterable<Pessoa> pessoasit = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasit);
		
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
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
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa, @RequestParam("pesquisasexo") String pesquisasexo) {
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		if(pesquisasexo != null && !pesquisasexo.isEmpty()) {
			
			pessoas = pessoaRepository.findPessoaByNameAndSex(nomepesquisa, pesquisasexo);
			
			
		}else {
			
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
		}

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaobj", new Pessoa());

		return modelAndView;
		
	}
	
	@GetMapping("**/pesquisarpessoa")
	public void imprimePdf(@RequestParam("nomepesquisa") String nomepesquisa, @RequestParam("pesquisasexo") String pesquisasexo, HttpServletRequest request, HttpServletResponse response) throws Exception{
			
		/*
		 * Verificar os parametros de download do pdf que estão com divergência
		 * 
		 */
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		if(pesquisasexo != null && !pesquisasexo.isEmpty() && nomepesquisa != null && !nomepesquisa.isEmpty()) { /*Busca por nome e sexo*/
			
			pessoas = pessoaRepository.findPessoaByNameAndSex(nomepesquisa, pesquisasexo);
				
		}
		
		else if (nomepesquisa != null && !nomepesquisa.isEmpty()) { /*Busca somente por nome*/
			
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
			
		}
		
		else if (pesquisasexo != null && pesquisasexo.isEmpty()) { /*Busca somente por sexo*/
			
			pessoas = pessoaRepository.findAllBySexo(pesquisasexo);
			
		}else { /*Busca Todos*/
			//pessoas = pessoaRepository.findAllAsc();
			
			Iterable<Pessoa> iterator = pessoaRepository.findAll();
			
			for (Pessoa pessoa : iterator) {
				pessoas.add(pessoa);
			}
			
		}
		
		/*Chama o sereviço de geração de relatórios*/
		
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		
		/*Tamanho da resposta pro navegador*/
		response.setContentLength(pdf.length);
		
		/*Define na resposta o tipo do arquivo*/
		response.setContentType("application/octet-stream");
		
		/*definir o cabeçalho da resposta*/
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		
		/*finaliza a resposta pro navegador*/
		response.getOutputStream().write(pdf);
		
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

	// RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")
	@PostMapping("**/addfonePessoa/{pessoaid}")
	public ModelAndView addFonePessoa(@Valid Telefone telefone, BindingResult bindingResult, @PathVariable("pessoaid") Long pessoaid) {

		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		
		if (bindingResult.hasErrors()) {


			modelAndView.addObject("pessoaobj", pessoa);
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));

			List<String> msg = new ArrayList<String>();

			for (ObjectError objectError : bindingResult.getAllErrors()) {

				msg.add(objectError.getDefaultMessage());

			}

			modelAndView.addObject("msg", msg);

			return modelAndView;

		} else {

			telefone.setPessoa(pessoa);

			telefoneRepository.save(telefone);

			modelAndView.addObject("pessoaobj", pessoa);

			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));

			return modelAndView;
		}

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
