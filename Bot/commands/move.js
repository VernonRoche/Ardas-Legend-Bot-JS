const {SlashCommandBuilder} = require('@discordjs/builders');
const fs = require('fs');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('move')
        .setDescription('Move an entity')
        .addSubcommand(subcommand =>
            subcommand
                .setName('army')
                .setDescription('Move an army')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The army\'s name')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('start-region')
                        .setDescription('The region from where the army starts moving')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('destination-region')
                        .setDescription('The destination of the army')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('food-type')
                        .setDescription('The food used to pay the movement')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('character')
                .setDescription('Move a roleplay character')
                .addStringOption(option =>
                    option
                        .setName('character-name')
                        .setDescription('Name of the character')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('start-region')
                        .setDescription('The region from where the army starts moving')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('destination-region')
                        .setDescription('The destination of the army')
                        .setRequired(true)),
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('trader')
                .setDescription('Move an trading company')
                .addStringOption(option =>
                    option.setName('trader-name')
                        .setDescription('The trader\'s name')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('start-region')
                        .setDescription('The region from where the army starts moving')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('destination-region')
                        .setDescription('The destination of the army')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('food-type')
                        .setDescription('The food used to pay the movement')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('armed-company')
                .setDescription('Move an armed trading company')
                .addStringOption(option =>
                    option.setName('armed-company-name')
                        .setDescription('The armed company\'s name')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('start-region')
                        .setDescription('The region from where the army starts moving')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('destination-region')
                        .setDescription('The destination of the army')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('food-type')
                        .setDescription('The food used to pay the movement')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const path = './Bot/commands/subcommands/move/';
        const files = fs.readdirSync(path, (err, tmp_files) => tmp_files.filter(file => file.contains('move_')));
        const commands = {};
        for (const file of files) {
            const name = file.split('move_')[1].slice(0, -3);
            commands[name] = require('./subcommands/move/' + file);
        }
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};