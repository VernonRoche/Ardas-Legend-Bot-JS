const {SlashCommandBuilder} = require("@discordjs/builders");
const {addSubcommands} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('create')
        .setDescription('Creates an entity (RpChar, army, trader etc.)')
        .addSubcommand(subcommand =>
            subcommand
                .setName('army')
                .setDescription('Creates an army')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('claimbuild-name')
                        .setDescription('The name of the originating claimbuild')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('unit-list')
                        .setDescription('The list of units in the army')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('trader')
                .setDescription('Creates a trading company')
                .addStringOption(option =>
                    option.setName('trader-name')
                        .setDescription('The name of the trader')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('claimbuild-name')
                        .setDescription('The name of the originating claimbuild')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('rpchar')
                .setDescription('Creates a Roleplay Character')
                .addStringOption(option =>
                    option.setName('name')
                        .setDescription("Character's name")
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('title')
                        .setDescription("Character's title")
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('gear')
                        .setDescription("Character's gear")
                        .setRequired(true))
                .addBooleanOption(option =>
                    option.setName('pvp')
                        .setDescription('Should the character participate in PvP?')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('armed-company')
                .setDescription('Creates an armed company')
                .addStringOption(option =>
                    option.setName('armed-company-name')
                        .setDescription('The name of the armed company')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('trader-name')
                        .setDescription('The name of the trader')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('character-name')
                        .setDescription('The name of the character bound to army/trader')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = addSubcommands('create', true);
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};