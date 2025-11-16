package com.partcraft.back.scheduler;

import com.partcraft.back.repository.ComponentLinkRepository;
import com.partcraft.back.repository.ComponentPriceRepository;
import com.partcraft.back.service.ComponentPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ComponentPricesUpdateScheduler {
    private final ComponentLinkRepository componentLinkRepository;
    private final ComponentPriceService componentPriceService;

    public ComponentPricesUpdateScheduler(ComponentLinkRepository componentLinkRepository, ComponentPriceService componentPriceService) {
        this.componentLinkRepository = componentLinkRepository;
        this.componentPriceService = componentPriceService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void UpdateComponentPrices() {
        var allLinks = componentLinkRepository.findAll();

        for (var link : allLinks) {

        }
    }
}
