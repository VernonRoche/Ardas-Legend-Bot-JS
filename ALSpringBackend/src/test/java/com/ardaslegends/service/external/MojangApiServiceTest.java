package com.ardaslegends.service.external;

import com.ardaslegends.data.service.dto.player.UUIDConverterDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.external.MojangApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
public class MojangApiServiceTest {

    MojangApiService mojangApiService;

    @BeforeEach
    void setup() {
        mojangApiService = new MojangApiService();
    }


    /**
     * This is a really SUS test, normally you'd mock the request so that it doesnt depend on that endpoint working
     * Feel free to delete this test if it throws any errors or is annoying XD
     */
   /** @Test
    void ensureGetUUIDWorksACTUALLY() {

        UUIDConverterDto dto = new UUIDConverterDto("mirak441", "4cd6b222b3894fd59d85ac90aa2c2c46");

        var result = mojangApiService.getUUIDByIgn("mirak441");

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void ensureBadRequestExceptionGetsConvertedToServiceExceptionWhenInputIsInvalid() {
        String ign = "SDKLASKDSALKDFLSAKDLAKDALK";

        var result = assertThrows(ServiceException.class, () -> mojangApiService.getUUIDByIgn(ign));

        assertThat(result.getMessage()).contains("is invalid");
    }**/
}
