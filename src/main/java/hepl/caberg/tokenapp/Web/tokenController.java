package hepl.caberg.tokenapp.Web;

import hepl.caberg.tokenapp.TLS.requestToACS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Date;

@Controller
public class tokenController {
    final requestToACS requestToACS;
    private String token = null;

    @Autowired
    public tokenController(requestToACS requestToACS) {
        this.requestToACS = requestToACS;
    }


    @GetMapping({"/", "/token"})
    public String token(Model model, @RequestParam(name = "isError", defaultValue = "false") boolean isError) {
        model.addAttribute("tokenRequest", new tokenRequestConfig());
        model.addAttribute("isError", isError);
        return "token";
    }

    @PostMapping("/tokenRequest")
    public String tokenRequest(@ModelAttribute tokenRequestConfig tc, Model model) {
        System.out.println(tc.getPublicKey());

        byte[] signature = tc.getSignature();
        tokenRequestTemplate token = tc.getTokenRequestTemplate();

        try {
            var tokenAnswer = this.requestToACS.requestToACS(token, signature);
            if(tokenAnswer != null) {
                this.token = tokenAnswer;
                return "redirect:/tokenResult";
            }
            else {
                return "redirect:/token?isError=true";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/tokenResult")
    public String tokenResult(Model model) {
        if(this.token != null)
            model.addAttribute("token", this.token);
        else
            model.addAttribute("token", "Error while getting token");
        return "tokenResult";
    }
}
