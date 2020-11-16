package curso.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.repository.PessoaRepository;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@RequestMapping(method=RequestMethod.GET, value = "/cadastropessoa")
	public String inicio() {
		
		return "cadastro/cadastropessoa";
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/salvarpessoa")
	public ModelAndView salvar(Pessoa pessoa) {
		
		pessoaRepository.save(pessoa);
		

		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasit = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasit);
		
		//utilizado para salvar uma nova pessoa e já carregar a lista de pessoas cadastradas
		
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/listapessoas")
	public ModelAndView pessoas() {
		
		//exibe a view
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		//carrega a lista de pessoas
		Iterable<Pessoa> pessoasit = pessoaRepository.findAll();
		//objeto de retorno para a view
		modelAndView.addObject("pessoas", pessoasit);
		
		return modelAndView;
	}

}