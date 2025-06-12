-- 创建举报表
CREATE TABLE IF NOT EXISTS reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    file_id INT NOT NULL,
    reporter_id INT NOT NULL,
    reason TEXT NOT NULL,
    report_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0: 待处理, 1: 已处理-批准, 2: 已处理-驳回',
    handler_id INT NULL,
    handle_time TIMESTAMP NULL,
    handle_note TEXT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 尝试创建索引（如果索引已存在会报错，但不影响脚本继续执行）
CREATE INDEX idx_reports_file_id ON reports(file_id);
CREATE INDEX idx_reports_reporter_id ON reports(reporter_id);
CREATE INDEX idx_reports_status ON reports(status);
CREATE INDEX idx_reports_handle_time ON reports(handle_time);

-- 插入测试数据 (仅在表为空时)
DELETE FROM reports WHERE id IN (1,2,3);

INSERT INTO reports (id, file_id, reporter_id, reason, status)
VALUES (1, 1, 1, '这个文件包含不适当内容', 0);

INSERT INTO reports (id, file_id, reporter_id, reason, status, handler_id, handle_time, handle_note)
VALUES (2, 2, 2, '这个文件侵犯版权', 1, 1, NOW(), '已确认违规并处理');

INSERT INTO reports (id, file_id, reporter_id, reason, status, handler_id, handle_time, handle_note)
VALUES (3, 3, 3, '这个文件包含病毒', 2, 1, NOW(), '经检查无异常，驳回举报'); 