package com.ardaslegends.data.service.external;

import com.ardaslegends.data.service.dto.player.UUIDConverterDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@RequiredArgsConstructor
@Slf4j

@Service
public class MojangApiService {

    private static final String ignToUuidURL = "https://api.mojang.com/users/profiles/minecraft/%s";

    /**
     * Method executes a blocking(!) HTTP GET Request to the Mojang API,
     * @param ign ingame name of the user
     * @return the UUID tied to the IGN
     */
    public UUIDConverterDto getUUIDByIgn(String ign) {
        log.debug("Fetching UUID tied to ign [{}]", ign);

        Objects.requireNonNull(ign);

        String url = ignToUuidURL.formatted(ign);
        RestTemplate restTemplate = new RestTemplate();
        UUIDConverterDto result = null;
        try {
            result = restTemplate.getForObject(url, UUIDConverterDto.class);
        } catch (RestClientException restClientException){
            log.warn("Error fetching UUID [{}]", restClientException.getMessage());
            throw ServiceException.cannotReadEntityDueToExternalMojangError(restClientException);
        }

        if(result == null) {
            log.warn("No User found with IGN [{}] in Mojang Database!", ign);
            throw new IllegalArgumentException("No user with ign [%s] was found in Mojang's Database!".formatted(ign));
        }

        log.debug("Fetched UUID: result [{}]", result);

        return result;
    }

}
