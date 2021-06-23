package dev.projectg.geyserhub.module.menu.bedrock;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.SelectorLogger;
import dev.projectg.geyserhub.module.menu.Button;
import dev.projectg.geyserhub.module.menu.CommandUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BedrockForm {

    private final boolean isEnabled;

    private String title;
    private String content;
    private List<Button> allButtons;

    /**
     * Create a new bedrock selector form and initializes it with the current loaded config
     */
    protected BedrockForm(@Nonnull ConfigurationSection section) {
        isEnabled = load(section);
    }

    protected boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Initialize or refresh the server selector form
     */
    private boolean load(@Nonnull ConfigurationSection configSection) {

        SelectorLogger logger = SelectorLogger.getLogger();

        String title = configSection.getString("Title");
        String content = configSection.getString("Content");
        if (title == null || content == null) {
            logger.severe("Value of Bedrock-Selector.Title or Bedrock-Selector.Content has no value in the config for form: "  + configSection.getName() + "! Failed to create the bedrock selector form.");
            return false;
        }

        // Get our Buttons
        if (!(configSection.contains("Buttons", true) && configSection.isConfigurationSection("Buttons"))) {
            logger.warn("Form: " + configSection.getName() + " does not contain a Buttons section, unable to create form");
            return false;
        }
        ConfigurationSection buttonSection = configSection.getConfigurationSection("Buttons");
        Objects.requireNonNull(buttonSection);
        List<Button> buttons = getButtons(buttonSection);
        if (buttons.isEmpty()) {
            logger.warn("Failed to create any valid buttons of form: " + configSection.getName() + "! All listed buttons have a malformed section!");
            return false;
        } else {
            logger.debug("Finished adding buttons to form: " + configSection.getName());
        }

        // Only set everything once it has been validated
        this.title = title;
        this.content = content;
        this.allButtons = buttons;

        return true;
    }

    /**
     *  Get the server buttons and each button's server
     * @param configSection The configuration section to pull the data from
     * @return A list of Buttons, which may be empty.
     */
    private List<Button> getButtons(@Nonnull ConfigurationSection configSection) {
        SelectorLogger logger = SelectorLogger.getLogger();

        // Get the form name
        String formName;
        ConfigurationSection parent = configSection.getParent();
        if (parent == null) {
            formName = "null";
        } else {
            formName = parent.getName();
        }
        logger.debug("Getting buttons for form: " + formName);

        // Get all the defined buttons in the buttons section
        Set<String> allButtonIds = configSection.getKeys(false);
        if (allButtonIds.isEmpty()) {
            logger.warn("No buttons were listed for form: " + formName);
            return Collections.emptyList();
        }

        // Create a list of buttons. For every defined button with a valid server or command configuration, we add its button.
        List<Button> compiledButtons = new ArrayList<>();
        for (String buttonId : allButtonIds) {
            ConfigurationSection buttonInfo = configSection.getConfigurationSection(buttonId);
            if (buttonInfo == null) {
                // This will be null if the buttonId key isn't actually a configuration section
                logger.warn(buttonId + " was not added because it is not a configuration section!");
                continue;
            }

            if (buttonInfo.contains("Button-Text", true) && buttonInfo.isString("Button-Text")) {
                String buttonText = buttonInfo.getString("Button-Text");
                Objects.requireNonNull(buttonText);
                logger.debug(buttonId + " has Button-Text: " + buttonText);

                // Add image if specified
                FormImage image = null;
                if (buttonInfo.contains("ImageURL", true)) {
                    String imageURL = buttonInfo.getString("ImageURL");
                    Objects.requireNonNull(imageURL);
                    image = FormImage.of(FormImage.Type.URL, imageURL);
                    logger.debug(buttonId + " contains image with URL: " + image.getData());
                }

                // Add commands if specified
                List<String> commands = Collections.emptyList();
                if (buttonInfo.contains("Commands") && buttonInfo.isList("Commands")) {
                    if (buttonInfo.getStringList("Commands").isEmpty()) {
                        logger.warn(buttonId + " contains commands list but the list was empty.");
                    } else {
                        commands = buttonInfo.getStringList("Commands");
                        logger.debug(buttonId + " contains commands: " + commands);
                    }
                }

                // Add server if specified
                String serverName = null;
                if (buttonInfo.contains("Server") && buttonInfo.isString("Server")) {
                    serverName = buttonInfo.getString("Server");
                    Objects.requireNonNull(serverName);
                    logger.debug(buttonId + " contains BungeeCord target server: " + serverName);
                }

                compiledButtons.add(
                        new Button(buttonText)
                        .setImage(image)
                        .setCommands(commands)
                        .setServer(serverName));

                logger.debug(buttonId + " was successfully added.");
            } else {
                logger.warn(buttonId + " does not contain a valid Button-Text value, not adding.");
            }
        }

        return compiledButtons;
    }

    /**
     * Send the server selector
     * @param floodgatePlayer the floodgate player to send it to
     */
    protected void sendForm(@Nonnull FloodgatePlayer floodgatePlayer) {
        if (!isEnabled) {
            throw new AssertionError("Form: " + title + " that failed to load was called to be sent to a player!");
        }

        SelectorLogger logger = SelectorLogger.getLogger();

        Player player = Bukkit.getServer().getPlayer(floodgatePlayer.getCorrectUniqueId());
        if (player == null) {
            logger.severe("Unable to find a Bukkit Player for the given Floodgate Player: " + floodgatePlayer.getCorrectUniqueId().toString());
            return;
        }

        // Resolve any placeholders in the button text
        List<Button> formattedButtons = new ArrayList<>();
        for (Button rawButton : allButtons) {
            Button copiedButton = new Button(rawButton);
            copiedButton.setText(PlaceholderAPI.setPlaceholders(player, copiedButton.getText()));
            formattedButtons.add(copiedButton);
        }

        // Create the form
        SimpleForm serverSelector = SimpleForm.of(title, content, formattedButtons.stream().map(Button::getButtonComponent).collect(Collectors.toList()));

        // Set the response handler
        serverSelector.setResponseHandler((responseData) -> {
            SimpleFormResponse response = serverSelector.parseResponse(responseData);
            if (!response.isCorrect()) {
                // isCorrect() = !isClosed() && !isInvalid()
                // player closed the form or returned invalid info (see FormResponse)
                return;
            }

            Button button = formattedButtons.get(response.getClickedButtonId());

            if (!button.getCommands().isEmpty()) {
                // Get the commands from the list of commands and replace any playerName placeholders
                for (String command : button.getCommands()) {
                    CommandUtils.runCommand(PlaceholderAPI.setPlaceholders(player, command), player);
                }
            }

            if (button.getServer() != null) {
                // This should never be out of bounds considering its size is the number of valid buttons
                String serverName = button.getServer();
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(baos)) {
                    out.writeUTF("Connect");
                    out.writeUTF(serverName);
                    player.sendPluginMessage(GeyserHubMain.getInstance(), "BungeeCord", baos.toByteArray());
                    player.sendMessage(ChatColor.DARK_AQUA + "Trying to send you to: " + ChatColor.GREEN + serverName);
                } catch (IOException e) {
                    logger.severe("Failed to send a plugin message to Bungeecord!");
                    e.printStackTrace();
                }
            }
        });

        // Send the form to the floodgate player
        floodgatePlayer.sendForm(serverSelector);
    }
}
