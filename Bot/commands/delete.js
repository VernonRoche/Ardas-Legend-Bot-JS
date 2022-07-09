const {SlashCommandBuilder} = require("@discordjs/builders");
const fs = require("fs");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('delete')
        .setDescription('Deletes an entity')
        .addSubcommand(subcommand =>
            subcommand
                .setName('player')
                .setDescription('Deletes a player')
                .addStringOption(option =>
                    option.setName('discord-id')
                        .setDescription('The Discord ID of the player to delete')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('character')
                .setDescription('Deletes the character of a user')
                .addStringOption(option =>
                    option.setName('discord-id')
                        .setDescription('The Discord ID of the user')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command. Does not call addSubcommands() function because it has
        // only admin subcommands. Ideally a third parameter would be added to check or not for non-admin subcommands.
        const commands = {};
        const path = './commands/subcommands/admin/delete/';
        const files = fs.readdirSync(path, (err, tmp_files) => tmp_files.filter(file => file.contains('delete_')));
        for (const file of files) {
            const name = file.split('delete_')[1].slice(0, -3);
            commands[name] = require('./subcommands/admin/delete/' + file);
        }
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};
