const {SlashCommandBuilder} = require("@discordjs/builders");
const {capitalizeFirstLetters} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('embark')
        .setDescription('Lets an army/trader/character or armed company embark on a ship.')
        .addStringOption(option =>
            option.setName('passenger-name')
                .setDescription('The army\'s/trader\'s or character\'s name')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('claimbuild-name')
                .setDescription('The origin claimbuild\'s name')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('region')
                .setDescription('The destination sea region')
                .setRequired(true)),
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('passenger-name').toLowerCase());
        const claimbuild=capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const region=interaction.options.getString('region');
        await interaction.reply(`${name} has embarked on a ship from ${claimbuild} and sails to region ${region}.`);
    },
};