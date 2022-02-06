const { SlashCommandBuilder } = require('@discordjs/builders');
const {MessageEmbed} = require("discord.js");
const {capitalizeFirstLetters} = require("../utils/utilities");

// Needs to be further implemented.
// Reaction counting is currently not implemented.
module.exports = {
    data: new SlashCommandBuilder()
        .setName('character-application')
        .setDescription('Create a roleplay character')
        .addStringOption(option =>
         option.setName('character-name')
             .setDescription('Your character\'s name')
             .setRequired(true))
        .addStringOption(option =>
            option.setName('application')
                .setDescription('Paste your application here')
                .setRequired(true)),
    async execute(interaction) {
        const name=capitalizeFirstLetters(interaction.options.getString('character-name').toLowerCase());
        const application=interaction.options.getString('application');

        const application_embed = new MessageEmbed()
            .setTitle(`${name} character application.`)
            .setDescription(`${application}`)
            .setTimestamp();
        await interaction.reply({ embeds: [application_embed]});
        const message = await interaction.fetchReply();
        await message.react('👍');
        await message.react('👎');

       /* const filter = (reaction, user) => {
            return reaction.emoji.name === '👍';
        };

        message.awaitReactions({ filter, max: 2, time: 30000, errors: ['time'] })
            .then(collected => console.log(collected.size))
            .catch(collected => {
                const reactions = message.reactions.cache;
                console.log(reactions.get('👍'));
                console.log(`After a minute, only ${collected.size} out of 5 reacted.`);
            });*/


        return 1;
    },
};