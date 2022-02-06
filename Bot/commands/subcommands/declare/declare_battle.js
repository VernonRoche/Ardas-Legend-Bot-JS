// Needs further implementation to ping all concerned users
// By fetching the assigned users for each participating army
const {capitalizeFirstLetters} = require("../../../utils/utilities");
module.exports = {
    async execute(interaction) {
        const target=capitalizeFirstLetters(interaction.options.getString('target').toLowerCase());
        const attacker=interaction.options.getString('attacker-list').toLowerCase();
        const defender=interaction.options.getString('defender-list').toLowerCase();
        const warcamp_coordinates=interaction.options.getString('war-camp-coordinates').toLowerCase();
        const author = interaction.user.username;
        if (target.equals('Fieldbattle')){
            await interaction.reply(`@${author} declared a field battle. The attackers will consist of ${attacker}
                fighting against ${defender}.`);
        }
        else{
            let warcamp = '';
            if (!(warcamp_coordinates==='')){
                warcamp = ` The attackers will be using a war camp built at ${warcamp_coordinates}`;
            }
            await interaction.reply(`@${author} declared a battle on ${target}. The attackers will consist of
                ${attacker} fighting against ${defender}.${warcamp}`);
        }
    },
};