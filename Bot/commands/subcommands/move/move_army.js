const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    // TO BE EXPANDED
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const food=capitalizeFirstLetters(interaction.options.getString('food-type').toLowerCase());
        const start=interaction.options.getInteger('start-region');
        const destination=interaction.options.getInteger('destination-region');
        await interaction.deferReply();
        await interaction.editReply(`${name} moved from ${start} to ${destination}, using ${food} for payment.`);
    },
};