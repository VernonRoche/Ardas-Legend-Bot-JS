const { SlashCommandBuilder } = require('@discordjs/builders');
const {capitalizeFirstLetters} = require("../utils/utilities");

// Needs to be further implemented.
// Reaction counting is currently not implemented.
module.exports = {
    data: new SlashCommandBuilder()
        .setName('visit')
        .setDescription('Visit a claimbuild with a roleplay character')
        .addStringOption(option =>
            option.setName('character-name')
                .setDescription('Your character\'s name')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('claimbuild-name')
                .setDescription('The claimbuild\'s name')
                .setRequired(true)),
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('character-name').toLowerCase());
        const claimbuild=capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        await interaction.reply(`${name} is now visiting ${claimbuild}.`);
    },
};