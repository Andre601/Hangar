package io.papermc.hangar.service.internal;

import io.papermc.hangar.db.dao.HangarDao;
import io.papermc.hangar.db.dao.internal.table.OrganizationDAO;
import io.papermc.hangar.model.db.OrganizationTable;
import io.papermc.hangar.service.HangarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService extends HangarService {

    private final OrganizationDAO organizationDAO;

    @Autowired
    public OrganizationService(HangarDao<OrganizationDAO> organizationDAO) {
        this.organizationDAO = organizationDAO.get();
    }

    public OrganizationTable getOrganizationTable(String ownerName) {
        return organizationDAO.getByUserName(ownerName);
    }
}