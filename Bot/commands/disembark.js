const {SlashCommandBuilder} = require("@discordjs/builders");
const {capitalizeFirstLetters} = require("../utils/utilities");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('disembark')
        .setDescription('Lets an army/trader/character or armed company disembark from a ship.')
        .addStringOption(option =>
            option.setName('passenger-name')
                .setDescription('The army\'s/trader\'s or character\'s name')
                .setRequired(true))
        .addIntegerOption(option =>
            option.setName('region')
                .setDescription('The destination land region')
                .setRequired(true)),
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('passenger-name').toLowerCase());
        const region=interaction.options.getInteger('region');
        await interaction.reply(`${name} has disembarked from a ship to region ${region}.`);
    },
};