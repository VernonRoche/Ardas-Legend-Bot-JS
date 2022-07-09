const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('cancel-move')
        .setDescription('Cancels a move. Beware that the army/character will stay at their current region.')
        .addSubcommand(subcommand =>
            subcommand
                .setName('character')
                .setDescription('Cancels a roleplay character move')
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('cancel', false);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};