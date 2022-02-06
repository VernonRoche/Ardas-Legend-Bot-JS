const { SlashCommandBuilder } = require('@discordjs/builders');
const {capitalizeFirstLetters} = require("../utils/utilities");

// Needs to be further implemented.
// Reaction counting is currently not implemented.
module.exports = {
    data: new SlashCommandBuilder()
        .setName('station')
        .setDescription('Station an army in a claimbuild')
        .addStringOption(option =>
            option.setName('army-name')
                .setDescription('Your army\'s name')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('claimbuild-name')
                .setDescription('The claimbuild\'s name')
                .setRequired(true)),
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const claimbuild=capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        await interaction.reply(`${name} is now stationed in ${claimbuild}.`);
    },
};