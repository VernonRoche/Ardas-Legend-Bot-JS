const {SlashCommandBuilder} = require("@discordjs/builders");
const fs = require("fs");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('heal')
        .setDescription('Starts or stops healing an army')
        .addSubcommand(subcommand =>
            subcommand
                .setName('start')
                .setDescription('Start healing an army')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
                .addStringOption(option =>
                    option.setName('claimbuild-name')
                        .setDescription('The name of the character')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('tokens')
                        .setDescription('How many tokens to heal the army')
                        .setRequired(true)
                        .setMaxValue(30)
                        .setMinValue(0))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('stop')
                .setDescription('Stop the healing of an army')
                .addStringOption(option =>
                    option.setName('army-name')
                        .setDescription('The name of the army')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const path = './Bot/commands/subcommands/heal/';
        const files = fs.readdirSync(path, (err, tmp_files) => tmp_files.filter(file => file.contains('heal_')));
        const commands = {};
        for (const file of files) {
            const name = file.split('heal_')[1].slice(0, -3);
            commands[name] = require('./subcommands/heal/' + file);
        }
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};