const {SlashCommandBuilder} = require('@discordjs/builders');
const {capitalizeFirstLetters} = require("../utils/utilities");
const {availableFactions, serverIP, serverPort} = require("../configs/config.json");
const {MessageEmbed} = require("discord.js");
const {REGISTER} = require("../configs/embed_thumbnails.json");
const axios = require("axios");

// Needs to be further implemented.
// Reaction counting is currently not implemented.
module.exports = {
    data: new SlashCommandBuilder()
        .setName('register')
        .setDescription('Register in the roleplay system')
        .addStringOption(option =>
            option.setName('ign')
                .setDescription('Your minecraft in-game name (IGN)')
                .setRequired(true))
        .addStringOption(option =>
            option.setName('faction-name')
                .setDescription('The faction you want to join')
                .setRequired(true)),
    async execute(interaction) {
        const ign = interaction.options.getString('ign');
        const faction = capitalizeFirstLetters(interaction.options.getString('faction-name').toLowerCase());

        if (!availableFactions.includes(faction)) {
            await interaction.reply({content: `${faction} is not a valid faction.`, ephemeral: true});
            await interaction.followUp({
                content: `Available factions: ${availableFactions.join(', ')}`,
                ephemeral: true
            });
        } else {
            // send to server
            const data = {
                ign: ign,
                discordID: interaction.member.id,
                faction: faction
            }

            axios.post('http://'+serverIP+':'+serverPort+'/api/player/create', data)
                .then(async function (response) {
                    // The request and data is successful.
                    const replyEmbed = new MessageEmbed()
                        .setTitle(`Register`)
                        .setColor('GREEN')
                        .setDescription(`You were successfully registered as ${ign} in the faction ${faction}.`)
                        .setThumbnail(REGISTER)
                        .setTimestamp()
                    await interaction.reply({embeds: [replyEmbed], ephemeral: true});
                })
                .catch(async function (error) {
                    // An error occurred during the request.
                    await interaction.reply({content: `${error.response.data.message}`, ephemeral: true});
                })

        }
    },
};