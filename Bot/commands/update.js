const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('update')
        .setDescription('Updates information about an entity')
        .addSubcommand(subcommand =>
            subcommand
                .setName('faction')
                .setDescription('Update your faction')
                .addStringOption(option =>
                    option.setName('faction-name')
                        .setDescription('The name of the faction')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('ign')
                .setDescription('Update your minecraft IGN')
                .addStringOption(option =>
                    option.setName('ign')
                        .setDescription('Your new IGN')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('discord-id')
                .setDescription('Update the discord ID of a player')
                .addStringOption(option =>
                    option.setName('old-discord-id')
                        .setDescription('The player\'s old discord ID')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('new-discord-id')
                        .setDescription('The player\'s new discord ID')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('rpchar-name')
                .setDescription('Update the name of your Roleplay Character')
                .addStringOption(option =>
                    option.setName('new-name')
                        .setDescription('The new name of your rp char')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('rpchar-title')
                .setDescription('Update the title of your Roleplay Character')
                .addStringOption(option =>
                    option.setName('new-title')
                        .setDescription('The new title of your rp char')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('rpchar-gear')
                .setDescription('Update the gear of your Roleplay Character')
                .addStringOption(option =>
                    option.setName('new-gear')
                        .setDescription('The new gear of your rp char')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('rpchar-pvp')
                .setDescription('Update the PvP status of your Roleplay Character')
                .addBooleanOption(option =>
                    option.setName('new-pvp')
                        .setDescription('The new PvP status of your rp char')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('update', true);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};
