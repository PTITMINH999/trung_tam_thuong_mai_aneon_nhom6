
package com.company.aneon;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Configuration
public class FlywayRepairConfiguration {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy(
            @Value("${ANEON_FLYWAY_REPAIR:false}") boolean repairEnabled,
            DataSource dataSource) {

        return (Flyway flyway) -> {

            // Chế độ bình thường: chỉ chạy migration
            if (!repairEnabled) {
                flyway.migrate();
                return;
            }

            System.out.println("===== FLYWAY REPAIR MODE =====");

            try (Connection connection = dataSource.getConnection()) {

                // Kiểm tra cột phone_lookup
                String columnSql = """
                    SELECT COUNT(*)
                    FROM information_schema.columns
                    WHERE table_schema = DATABASE()
                    AND table_name = 'khach_mua_sam'
                    AND column_name = 'phone_lookup'
                    """;

                // Kiểm tra hai index của V5
                String indexSql = """
                    SELECT COUNT(DISTINCT index_name)
                    FROM information_schema.statistics
                    WHERE table_schema = DATABASE()
                    AND table_name = 'khach_mua_sam'
                    AND index_name IN (
                        'ix_khach_mua_sam_phone_lookup',
                        'ix_khach_mua_sam_name_lookup'
                    )
                    """;

                int columnCount;
                int indexCount;

                try (PreparedStatement ps =
                             connection.prepareStatement(columnSql);
                     ResultSet rs = ps.executeQuery()) {

                    rs.next();
                    columnCount = rs.getInt(1);
                }

                try (PreparedStatement ps =
                             connection.prepareStatement(indexSql);
                     ResultSet rs = ps.executeQuery()) {

                    rs.next();
                    indexCount = rs.getInt(1);
                }

                System.out.println(
                    "phone_lookup column count: " + columnCount
                );

                System.out.println(
                    "V5 index count: " + indexCount
                );

                // Không repair nếu V5 đã tạo một phần
                if (columnCount > 0 || indexCount > 0) {
                    throw new IllegalStateException(
                        "V5 has partial database changes. " +
                        "Repair cancelled for safety."
                    );
                }

            } catch (Exception e) {
                throw new IllegalStateException(
                    "Cannot verify database before Flyway repair", e
                );
            }

            // Chỉ repair sau khi kiểm tra an toàn
            System.out.println("Starting Flyway repair...");

            flyway.repair();

            System.out.println("Flyway repair completed.");

            // Chạy lại các migration chưa thành công
            flyway.migrate();

            System.out.println("Flyway migration completed.");
        };
    }
}
