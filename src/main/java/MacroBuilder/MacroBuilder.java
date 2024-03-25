package MacroBuilder;

import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.message.component.SelectMenuOptionBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MacroBuilder {

   public void sendMacroMessage(MessageCreateEvent event) {
      List<SelectMenuOption> optionList = new ArrayList<>();
      MessageBuilder message = new MessageBuilder()
         .setContent("Test");
      message.addComponents(ActionRow.of(SelectMenu.createStringMenu(
         "options", Arrays.asList(
            SelectMenuOption.create("Option 1", "/cast"),
            SelectMenuOption.create("Option 2", "/use"))
      )));
      message.send(event.getChannel());
   }
}
