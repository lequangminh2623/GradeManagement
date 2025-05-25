import { useState } from 'react';
import { Table, Card, Row, Col } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';

const SemesterTable = ({ semesterTitle, subjects, summary }) => {
    const [isHovered, setIsHovered] = useState(false);
    const { t } = useTranslation()

    return (
        <Card className="mb-4 shadow-sm rounded-3" onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}>
            <Card.Header className={`${isHovered ? `bg-primary text-white` : ''} transition-colors text-center py-3`} as="h5">
                {semesterTitle}
            </Card.Header>
            <Card.Body>
                <Table bordered hover responsive className="align-middle text-nowrap">
                    <thead className={`${isHovered ? `table-primary` : ''} trasition-colors`}>
                        <tr>
                            <th>#</th>
                            <th>{t('course-code')}</th>
                            <th>{t('classrooms')}</th>
                            <th>{t('course')}</th>
                            <th>{t('credit')}</th>
                            <th>{t('extra-grades')}</th>
                            <th>{t('midterm-grade')}</th>
                            <th>{t('final-grade')}</th>
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
                                    {s.extraGrade.length > 0 ? s.extraGrade.map((g, index) => <span key={index}>{g}&nbsp;&nbsp;</span>) : '-'}
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
}

export default SemesterTable;