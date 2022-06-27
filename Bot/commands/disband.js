const {SlashCommandBuilder} = require("@discordjs/builders");
const fs = require("fs");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('disband')
        .setDescription('Disbands an entity (trader, army etc.)')
        .addSubcommand(subcommand =>
            subcommand
                .setName('army')
                .setDescription('Disbands an army')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('trader')
                .setDescription('Disbands a trader company')
                .addStringOption(option =>
                    option.setName('trader-name')
                        .setDescription('The name of the trader company')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('armed-company')
                .setDescription('Breaks the armed company into separate trader and army. Character gets unbound.')
                .addStringOption(option =>
                    option.setName('armed-company-name')
                        .setDescription('The name of the armed company')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        addSubcommands('disband', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};