package com.ltts.project.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.ltts.project.Dao.MemberDao;
import com.ltts.project.Dao.PlayerDao;
import com.ltts.project.model.Member;
import com.ltts.project.model.Player;

@SuppressWarnings("unchecked")
@RestController
public class HomeController {
	private static final String MY_SESSION_NOTES_CONSTANT = "MY_SESSION_NOTES";
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
	@Autowired
	MemberDao md;
	
	@Autowired
	PlayerDao pd;
	
	@RequestMapping("/hi")
	public String firstMethod() {
		return "Hello SpringBoot";
	}
	
	@RequestMapping(" ")
	public ModelAndView secondMethod() {
		return new ModelAndView("index");
	}
	
	@PostMapping(value ="/destroy/session")
    public String destroySession(final HttpServletRequest request) {
        log.info("Invaliding the session and removing the data.");
        // Invalidate the session and this will clear the data from the configured database.
        request.getSession().invalidate();
        return "redirect:/";
    }
	@RequestMapping("/loginregister")
	public ModelAndView loginregistermethod(@RequestParam("email") final String email,@RequestParam("pass") final String pass,@RequestParam("action") String action,final HttpServletRequest request,final HttpSession session,final Model model) {
		if ("register".equals(action)) {
		final List<String> notes = (List<String>) session.getAttribute(MY_SESSION_NOTES_CONSTANT);
	    model.addAttribute("sessionNotes", !CollectionUtils.isEmpty(notes) ? notes : new ArrayList<>());
		return new ModelAndView("registration");
		}
		else if ("login".equals(action)) {
			List<String> notes = (List<String>) request.getSession().getAttribute(MY_SESSION_NOTES_CONSTANT);
	       
	        if (CollectionUtils.isEmpty(notes)) {
	            log.info("No notes are fetch from the session object. Setting the value in the session object.");
	            notes = new ArrayList<>();
	        }
	        notes.add(email);
	        notes.add(pass);
	    request.getSession().setAttribute(MY_SESSION_NOTES_CONSTANT, notes);
		return new ModelAndView("login");
		}
		return null;
	
	}

	@RequestMapping("/registration")
	public ModelAndView registerUser(final Model model, final HttpSession session) {
		 final List<String> notes = (List<String>) session.getAttribute(MY_SESSION_NOTES_CONSTANT);
	        model.addAttribute("sessionNotes", !CollectionUtils.isEmpty(notes) ? notes : new ArrayList<>());
		return new ModelAndView("registration");
	}
	
	@RequestMapping("/login") 
	public ModelAndView login(@RequestParam("email") final String email,@RequestParam("pass") final String pass ,final HttpServletRequest request) {
		  List<String> notes = (List<String>) request.getSession().getAttribute(MY_SESSION_NOTES_CONSTANT);
	        // Check if notes is present in session or not.
	        if (CollectionUtils.isEmpty(notes)) {
	            log.info("No notes are fetch from the session object. Setting the value in the session object.");
	            notes = new ArrayList<>();
	        }
	        notes.add(email);
	        notes.add(pass);
	        request.getSession().setAttribute(MY_SESSION_NOTES_CONSTANT, notes);
		return new ModelAndView("login");
	}
	
	@RequestMapping("/addplayer")
	public ModelAndView addPlayerPage() {
		return new ModelAndView("addplayer");
	}
	
	
	@RequestMapping(value="adduser", method=RequestMethod.POST)
	public ModelAndView addUser(HttpServletRequest req, Model model) {
		ModelAndView mv=null;
		String name=req.getParameter("uname");
		String pass=req.getParameter("pass");
		String email=req.getParameter("email");
		String mobile=req.getParameter("mobile");
		
		Member m=new Member(name,pass,email,mobile);
		System.out.println("***** INSIDE CONTROLLER ****"+m);
		boolean b=md.InsertMember(m);
		if(b==false) {
			mv=new ModelAndView("success");
			model.addAttribute("msg", "Successfully Inserted.. ");
		}
		else {
			mv=new ModelAndView("error");
			model.addAttribute("msg", "Error due to Connection");
			
		}
		
		return mv;
	}
	
	@RequestMapping(value="checkuser")
	public ModelAndView checkUser(HttpServletRequest req, Model model) {
		ModelAndView mv=null;
		String email=req.getParameter("lemail");
		String pass=req.getParameter("lpass");
		
		Member m=md.getMemberByEmai(email);
		System.out.println(m);
		
		
		if(m !=null) {
		
			if(pass.equals(m.getPassword())) {
				model.addAttribute("value", m.getUserName());
				mv=new ModelAndView("welcome");
			}
			else {
				model.addAttribute("msg", "Password Wrong");
				mv=new ModelAndView("login");
			}
		}
		else {
			model.addAttribute("msg", "User Not Found Please Register");
			mv=new ModelAndView("login");
		}
		return mv;
	}
	
	
	
	
	@RequestMapping(value="insertplayer", method=RequestMethod.POST)
	public ModelAndView insertPlayer(HttpServletRequest req, Model model) {
		ModelAndView mv=null;
		String name=req.getParameter("playername");
		String mobile=req.getParameter("playermobile");
		Player p=new Player(1,name,mobile);
		System.out.println("***** INSIDE CONTROLLER ****"+p);
		boolean b=pd.insertPlayer(p);
		if(b==false) {
			mv=new ModelAndView("success");
			model.addAttribute("msg", "Player Successfully Inserted.. ");
		}
		else {
			mv=new ModelAndView("error");
			model.addAttribute("msg", "Error due to Connection");
			
		}
		
		return mv;
	}
	
	@RequestMapping("/viewplayers")
	public ModelAndView viewAllPlayers(Model model) {
		ModelAndView mv=new ModelAndView("viewplayers");
		List<Player> li=pd.getAllPlayers();
		
		model.addAttribute("list", li);
		
		return mv;
	}
}
