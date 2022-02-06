const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('trader-name').toLowerCase());
        const character=capitalizeFirstLetters(interaction.options.getString('character-name').toLowerCase());
        await interaction.reply(`${character} has been bound to the trading company ${name}`);
    },
};