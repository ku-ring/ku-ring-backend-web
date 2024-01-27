package com.kustacks.kuring.admin.application.port.out;

import com.kustacks.kuring.admin.domain.Admin;

public interface AdminCommandPort {
    void save(Admin admin);
}
