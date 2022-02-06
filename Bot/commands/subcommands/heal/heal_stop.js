const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        await interaction.reply(`${name} has stopped healing.`);
    },
};