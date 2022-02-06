const {SlashCommandBuilder} = require("@discordjs/builders");
const {capitalizeFirstLetters} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('pick-siege')
        .setDescription('Pick a siege equipment with an army or armed company')
        .addStringOption(option =>
            option.setName('army-name')
                .setDescription('The army\'s name')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('claimbuild-name')
                .setDescription('The claimbuild\'s name')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('siege-name')
                .setDescription('The siege equipment chosen')
                .setRequired(true)),
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const claimbuild=capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const siege=capitalizeFirstLetters(interaction.options.getString('siege-name').toLowerCase());
        await interaction.reply(`${name} has picked up siege equipment (${siege}) at ${claimbuild}.`);
    },
};