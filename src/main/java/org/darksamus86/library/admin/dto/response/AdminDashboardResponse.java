package org.darksamus86.library.admin.dto.response;

import java.util.Map;

public record AdminDashboardResponse(
        long totalUsers,
        long activeUsers,
        long totalBooks,
        long activeBooks,
        Map<String, Long> usersByRole
) {}
