const {capitalizeFirstLetters} = require("../../../utils/utilities");

module.exports = {
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('armed-company-name').toLowerCase());
        const army=capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const trader=capitalizeFirstLetters(interaction.options.getString('trader-name').toLowerCase());
        const character=capitalizeFirstLetters(interaction.options.getString('character-name').toLowerCase());
        await interaction.reply(`The armed company ${name} comprised of the army ${army} and trading company ${trader},
         has been created and bound to ${character}.`);
    },
};