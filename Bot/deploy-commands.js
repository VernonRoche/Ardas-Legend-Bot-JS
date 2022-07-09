const fs = require('fs');
const {REST} = require('@discordjs/rest');
const {Routes} = require('discord-api-types/v9');
const {clientId, guildId} = require('./configs/config.json');
const {token} = require('./configs/bot_token.json');


const commands = [];
const commandFiles = fs.readdirSync('./commands').filter(file => file.endsWith('.js'));
const adminCommandFiles = fs.readdirSync('./commands/admin').filter(file => file.endsWith('.js'));

for (const file of commandFiles) {
    const command = require(`./commands/${file}`);
    commands.push(command.data.toJSON());
}

for (const file of adminCommandFiles) {
    const command = require(`./commands/admin/${file}`);
    commands.push(command.data.toJSON());
}


// noinspection JSCheckFunctionSignatures,JSClosureCompilerSyntax
const rest = new REST({version: '9'}).setToken(token);


(async () => {
    try {
        console.log('Started refreshing application (/) commands.');

        await rest.put(
            Routes.applicationGuildCommands(clientId, guildId),
            {body: commands},
        );

        console.log('Successfully reloaded application (/) commands.');
    } catch (error) {
        console.error(error);
    }
})();