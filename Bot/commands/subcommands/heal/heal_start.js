const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const claimbuild_name=capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const tokens = interaction.options.getInteger('tokens');
        await interaction.reply(`${name} has started healing ${tokens} in ${claimbuild_name}.`);
    },
};