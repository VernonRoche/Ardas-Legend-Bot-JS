const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('character-name').toLowerCase());
        const start=interaction.options.getInteger('start-region');
        const destination=interaction.options.getInteger('destination-region');
        await interaction.deferReply();
        await interaction.editReply(`${name} moved from ${start} to ${destination}`);
    },
};