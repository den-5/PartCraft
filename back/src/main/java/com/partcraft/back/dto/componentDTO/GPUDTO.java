package com.partcraft.back.dto.componentDTO;

import com.partcraft.back.dto.componentDTO.helper.Size;
import com.partcraft.back.entity.component.GPU;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GPUDTO {
    private Long id;
    private String gpuBrand;
    private String gpuModel;
    private Integer gpuMemoryGb;
    private String pictureUrl;
    private Size size;
    private Integer powerDraw;

    public GPUDTO(GPU gpu) {
        this.id = gpu.getId();
        this.gpuBrand = gpu.getGpuBrand();
        this.gpuModel = gpu.getGpuModel();
        this.gpuMemoryGb = gpu.getGpuMemoryGb();
        this.pictureUrl = gpu.getPictureUrl();
        if (gpu.getSize() != null) {
            this.size = new Size(gpu.getSize().getWidth(), gpu.getSize().getLength(), gpu.getSize().getHeight());
        } else {
            this.size = null;
        }
        this.powerDraw = gpu.getPowerDraw();
    }
}
