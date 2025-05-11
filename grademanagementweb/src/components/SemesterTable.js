import { Table, Card, Row, Col } from 'react-bootstrap';

const SemesterTable = ({ semesterTitle, subjects, summary }) => (
    <Card className="mb-4 shadow-sm rounded-3">
        <Card.Header className="bg-primary text-white text-center py-3" as="h5">
            {semesterTitle}
        </Card.Header>
        <Card.Body>
            <Table bordered hover responsive className="align-middle text-nowrap">
                <thead className="table-primary">
                    <tr>
                        <th>#</th>
                        <th>Mã học phần</th>
                        <th>Tên lớp</th>
                        <th>Tên học phần</th>
                        <th>Số tín chỉ</th>
                        <th>Điểm bổ sung</th>
                        <th>Điểm giữa kỳ</th>
                        <th>Điểm cuối kỳ</th>
                    </tr>
                </thead>
                <tbody>
                    {subjects.map((s, idx) => (
                        <tr key={idx}>
                            <td>{idx + 1}</td>
                            <td>{s.code}</td>
                            <td>{s.classCode}</td>
                            <td>{s.name}</td>
                            <td>{s.credit}</td>
                            <td>
                                {s.extraGrade.length > 0 ? s.extraGrade.map(g => `${g} `) : '-'}
                            </td>
                            <td>{s.midTermGrade || '-'}</td>
                            <td>{s.finalGrade || '-'}</td>
                        </tr>
                    ))}
                </tbody>
            </Table>
            {summary && (
                <div className="mt-4">
                    <Row>
                        <Col sm={6}>
                            <strong>Điểm trung bình tích lũy hệ 4:</strong> {summary.gpa}
                        </Col>
                        <Col sm={6}>
                            <strong>Số tín chỉ tích lũy:</strong> {summary.credits}
                        </Col>
                    </Row>
                </div>
            )}
        </Card.Body>
    </Card>
);

export default SemesterTable;