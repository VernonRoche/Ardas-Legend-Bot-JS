const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const claimbuild=capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const units=capitalizeFirstLetters(interaction.options.getString('unit-list').toLowerCase());
        await interaction.reply(`The army ${name} comprised of ${units}, has been created at ${claimbuild}.`);
    },
};