// repository/ReportDetailRepository.java
package com.example.ReportService.repository;

import com.example.ReportService.model.ReportDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportDetailRepository
        extends JpaRepository<ReportDetail, Long> {

    // Spring genera: SELECT * FROM report_details WHERE report_id = ?
    List<ReportDetail> findByReportId(Long reportId);

    // Spring genera: SELECT * FROM report_details WHERE categoria = ?
    List<ReportDetail> findByCategoria(String categoria);

    // Spring genera: SELECT * FROM report_details
    //                WHERE report_id = ? AND categoria = ?
    List<ReportDetail> findByReportIdAndCategoria(
            Long reportId, String categoria);

    // Spring genera: SELECT * FROM report_details
    //                WHERE LOWER(descripcion) LIKE LOWER('%texto%')
    List<ReportDetail> findByDescripcionContainingIgnoreCase(
            String descripcion);

    // Spring genera: SELECT * FROM report_details
    //                WHERE report_id = ? ORDER BY valor DESC
    List<ReportDetail> findByReportIdOrderByValorDesc(Long reportId);

    // Spring genera: SELECT * FROM report_details ORDER BY id DESC LIMIT 10
    List<ReportDetail> findTop10ByOrderByIdDesc();
}