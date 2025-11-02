package com.partcraft.back.dto.componentDTO;

import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.component.RAMKit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RAMKitDTO {
    private Long id;
    private Integer ramSizeGb;
    private String ramType;
    private Integer ramSpeedMhz;
    private Integer ramSticksCount;
    private String pictureUrl;
    private Size size;

    public RAMKitDTO(RAMKit ramKit) {
        this.id = ramKit.getId();
        this.ramSizeGb = ramKit.getRamSizeGb();
        this.ramType = ramKit.getRamType();
        this.ramSpeedMhz = ramKit.getRamSpeedMhz();
        this.ramSticksCount = ramKit.getRamSticksCount();
        this.pictureUrl = ramKit.getPictureUrl();
        if (ramKit.getSize() != null) {
            this.size = new Size(ramKit.getSize().getWidth(), ramKit.getSize().getLength(), ramKit.getSize().getHeight());
        } else {
            this.size = null;
        }
    }
}
