package hepl.caberg.tokenapp.Web;

import hepl.caberg.tokenapp.TLS.requestToACS;
import hepl.caberg.tokenapp.tokens.tokenRequestConfig;
import hepl.caberg.tokenapp.tokens.tokenRequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class tokenController {
    final requestToACS requestToACS;
    private String token = null;

    @Autowired
    public tokenController(requestToACS requestToACS) {
        this.requestToACS = requestToACS;
    }

    // This page displays the form
    @GetMapping({"/", "/token"})
    public String token(Model model, @RequestParam(name = "isError", defaultValue = "false") boolean isError) {
        model.addAttribute("tokenRequest", new tokenRequestConfig());
        model.addAttribute("isError", isError);
        return "token";
    }

    // This method is called when the user submits the form
    // A request to the ACS is made
    @PostMapping("/tokenRequest")
    public String tokenRequest(@ModelAttribute tokenRequestConfig tc, Model model) {
        // Let's gather the data
        byte[] signature = tc.getSignature();
        tokenRequestTemplate token = tc.getTokenRequestTemplate();

        try {
            // Let's send the data to the ACS
            var tokenAnswer = this.requestToACS.requestToACS(token, signature);

            // The encapsulated request returns null if the server sends a NACK
            if(tokenAnswer != null) {
                // We store the token in the controller and redirect to the result page
                this.token = tokenAnswer;
                return "redirect:/tokenResult";
            }
            else {
                // We redirect to the token page with an error
                return "redirect:/token?isError=true";
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This page displays the token
    @GetMapping("/tokenResult")
    public String tokenResult(Model model) {
        if(this.token != null)
            model.addAttribute("token", this.token);
        else
            model.addAttribute("token", "Error while getting token");
        return "tokenResult";
    }
}
