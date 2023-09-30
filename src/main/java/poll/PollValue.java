package poll;

import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;

import java.util.ArrayList;
import java.util.List;

public class PollValue {
   SlashCommandCreateEvent event;
   private boolean multipleChoice;
   private String titel;
   private String question;
   private ServerTextChannel textChannel = null;
   private String privacy = "";
   private List<String> answereList = new ArrayList<>();
   private List<Integer> timeList = new ArrayList<>();
   private List<User> userList1 = new ArrayList<>();

   public PollValue(SlashCommandCreateEvent event,
                    boolean multipleChoice,
                    String titel,
                    String question,
                    ServerChannel channel,
                    ServerTextChannel textChannel,
                    String privacy,
                    List<String> answereList,
                    List<Integer> timeList,
                    List<User> userList1) {
      this.event = event;
      this.multipleChoice = multipleChoice;
      this.titel = titel;
      this.question = question;
      this.textChannel = textChannel;
      this.privacy = privacy;
      this.answereList = answereList;
      this.timeList = timeList;
      this.userList1 = userList1;
   }

   public SlashCommandCreateEvent getEvent() {
      return event;
   }

   public void setEvent(SlashCommandCreateEvent event) {
      this.event = event;
   }

   public boolean isMultipleChoice() {
      return multipleChoice;
   }

   public void setMultipleChoice(boolean multipleChoice) {
      this.multipleChoice = multipleChoice;
   }

   public String getTitel() {
      return titel;
   }

   public void setTitel(String titel) {
      this.titel = titel;
   }

   public String getQuestion() {
      return question;
   }

   public void setQuestion(String question) {
      this.question = question;
   }

   public ServerTextChannel getTextChannel() {
      return textChannel;
   }

   public void setTextChannel(ServerTextChannel textChannel) {
      this.textChannel = textChannel;
   }

   public String getPrivacy() {
      return privacy;
   }

   public void setPrivacy(String privacy) {
      this.privacy = privacy;
   }

   public List<String> getAnswereList() {
      return answereList;
   }

   public void setAnswereList(List<String> answereList) {
      this.answereList = answereList;
   }

   public List<Integer> getTimeList() {
      return timeList;
   }

   public void setTimeList(List<Integer> timeList) {
      this.timeList = timeList;
   }

   public List<User> getUserList1() {
      return userList1;
   }
   public void setUserList1(List<User> userList1) {
      this.userList1 = userList1;
   }

}
