import { useCallback, useEffect, useState } from "react";
import { Table, Button, Form, Row, Col } from "react-bootstrap";
import { useParams, useSearchParams } from "react-router-dom";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layouts/MySpinner";
import { FaSave, FaPlus, FaTimes, FaUpload, FaLock, FaFileCsv, FaFilePdf } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import { capitalizeFirstWord } from "../../utils/utils"


const MAX_EXTRA_GRADES = 3;

const ClassroomDetail = () => {
    const { classroomId } = useParams();

    // thêm state cho info lớp
    const [classInfo, setClassInfo] = useState({
        classroomName: "",
        lecturerName: "",
        courseName: "",
        academicTerm: "",
        gradeStatus: "",
    });
    const [students, setStudents] = useState([]);
    // Thêm state lưu lỗi điểm cho từng sinh viên
    const [gradeErrors, setGradeErrors] = useState({});
    const [extraCount, setExtraCount] = useState(0);
    const [loading, setLoading] = useState(false);
    const [q] = useSearchParams();
    const [page, setPage] = useState(1);
    const displayCount = Math.max(extraCount, 1);
    const { t } = useTranslation()

    const fetchTranscript = useCallback(async () => {
        try {
            setLoading(true);

            let url = `${endpoints["classroom-details"](classroomId)}?page=${page}`;

            const kw = q.get('kw');
            if (kw) {
                url += `&kw=${kw}`;
            }

            const res = await authApis().get(url);
            const data = res.data;

            setClassInfo({
                classroomName: data.classroomName,
                lecturerName: data.lecturerName,
                courseName: data.courseName,
                academicTerm: data.academicTerm,
                gradeStatus: data.gradeStatus,
            });

            const studentsCopy = data.students.map((s) => ({
                ...s,
                extraGrades: [...s.extraGrades],
            }));
            setStudents(studentsCopy);

            const maxEx = Math.min(
                MAX_EXTRA_GRADES,
                Math.max(0, ...studentsCopy.map((s) => s.extraGrades.length))
            );
            setExtraCount(maxEx);

        } catch (err) {
            console.error("Lỗi khi lấy bảng điểm:", err);
        } finally {
            setLoading(false);
        }
    }, [classroomId, page, q]);

    useEffect(() => {
        if (page > 0) {
            fetchTranscript();
        }
    }, [page, q, fetchTranscript]);

    const addExtra = () => {
        if (extraCount < MAX_EXTRA_GRADES) {
            setExtraCount(extraCount + 1);
            setStudents((prev) =>
                prev.map((s) => ({ ...s, extraGrades: [...s.extraGrades, null] }))
            );
        }
    };

    const removeExtraAt = (idx) => {
        if (idx < 0 || idx >= extraCount) return;

        const confirmDelete = window.confirm("Bạn có chắc chắn muốn xóa cột điểm bổ sung này?");
        if (!confirmDelete) return;

        setExtraCount(extraCount - 1);
        setStudents((prev) =>
            prev.map((s) => {
                const arr = [...s.extraGrades];
                arr.splice(idx, 1);
                return { ...s, extraGrades: arr };
            })
        );
    };


    const handleChange = (studentId, field, value, idx) => {
        setStudents((prev) =>
            prev.map((s) => {
                if (s.studentId !== studentId) return s;
                const val = value === "" ? null : parseFloat(value);
                // Kiểm tra lỗi
                let error = "";
                if (val !== null && (val < 0 || val > 10)) {
                    error = "Điểm phải từ 0 đến 10";
                }
                setGradeErrors(prevErrs => {
                    const errs = { ...prevErrs };
                    if (!errs[studentId]) errs[studentId] = {};
                    if (field === "extra") {
                        if (!errs[studentId].extra) errs[studentId].extra = {};
                        errs[studentId].extra[idx] = error;
                    } else {
                        errs[studentId][field] = error;
                    }
                    return errs;
                });

                if (field === "mid") return { ...s, midtermGrade: val };
                if (field === "final") return { ...s, finalGrade: val };
                if (field === "extra") {
                    const arr = [...s.extraGrades];
                    arr[idx] = val;
                    return { ...s, extraGrades: arr };
                }
                return s;
            })
        );
    };

    const [selectedFile, setSelectedFile] = useState(null);

    const handleFileChange = (e) => {
        setSelectedFile(e.target.files[0]);
    };

    const uploadCsv = async () => {
        if (!selectedFile) {
            alert("Vui lòng chọn một file CSV!");
            return;
        }

        const formData = new FormData();
        formData.append("file", selectedFile);

        try {
            const res = await authApis().post(endpoints["classroom-import"](classroomId),
                formData,
                {
                    headers: {
                        "Content-Type": "multipart/form-data",
                    },
                }
            );

            alert(res.data);

            fetchTranscript();
        } catch (err) {
            alert(err.response?.data || "Lỗi khi upload file CSV");
        }
    };


    const saveGrades = async () => {
        setLoading(true);
        try {
            const payload = students.map(s => ({
                studentId: s.studentId,
                midtermGrade: s.midtermGrade,
                finalGrade: s.finalGrade,
                extraGrades: s.extraGrades.slice(0, extraCount)
            }));

            const res = await authApis().post(endpoints['classroom-details'](classroomId), payload);
            alert(res.data);
        } catch (err) {
            alert(err.response?.data || "Lỗi khi lưu điểm");
        } finally {
            setLoading(false);
        }
    };

    const lockGrades = async () => {
        const confirmed = window.confirm("Bạn có chắc chắn muốn khóa bảng điểm? Sau khi khóa sẽ không thể chỉnh sửa!");

        if (!confirmed) return;

        setLoading(true);
        try {
            const res = await authApis().patch(endpoints["classroom-lock"](classroomId));
            classInfo.gradeStatus = "LOCKED";
            setClassInfo({ ...classInfo });
            alert(res.data);
        } catch (err) {
            alert(err.response?.data || "Lỗi khi khóa bảng điểm");
        } finally {
            setLoading(false);
        }
    };

    const downloadFile = async (format) => {
        try {
            const response = await authApis().get(endpoints[`export-${format}`](classroomId), {
                responseType: 'blob',
                withCredentials: true,
            });
            if (response.status !== 200) {
                throw new Error('Failed to download file');
            }

            const blob = new Blob([response.data], { type: response.data.type });
            const downloadUrl = window.URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.href = downloadUrl;
            link.download = `grades_classroom_${classroomId}.${format}`;
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(downloadUrl);
        } catch (err) {
            console.error('Download error:', err);
            alert('Không thể tải file. Vui lòng thử lại.');
        }
    };

    const hasAnyGradeError = () => {
        for (const sid in gradeErrors) {
            if (!gradeErrors[sid]) continue;
            if (gradeErrors[sid].mid) return true;
            if (gradeErrors[sid].final) return true;
            if (gradeErrors[sid].extra) {
                for (const idx in gradeErrors[sid].extra) {
                    if (gradeErrors[sid].extra[idx]) return true;
                }
            }
        }
        return false;
    };

    if (loading) return <MySpinner />;

    return (
        <>
            <div className="d-flex justify-content-between flex-wrap mb-3">
                <h3 className="m-4">{capitalizeFirstWord(`${t('grades-table')} ${t('classrooms')}`)}</h3>

                <div className="m-4">
                    <Button
                        variant="outline-success"
                        onClick={() => downloadFile('csv')}
                        disabled={classInfo.gradeStatus !== "LOCKED"}
                        className="me-2 mb-2"
                    >
                        <FaFileCsv className="me-2" />
                        {t('export')} CSV
                    </Button>

                    <Button
                        variant="outline-danger"
                        onClick={() => downloadFile('pdf')}
                        disabled={classInfo.gradeStatus !== "LOCKED"}
                        className="mb-2"
                    >
                        <FaFilePdf className="me-2" />
                        {t('export')} PDF
                    </Button>
                </div>
            </div>

            <Form className="mb-4 p-3 border rounded bg-light">
                <Row>
                    <Col md={6}>
                        <Form.Group controlId="classroomName" className="mb-3">
                            <Form.Label>{t('classrooms')}:</Form.Label>
                            <Form.Control
                                type="text"
                                value={classInfo.classroomName}
                                readOnly
                            />
                        </Form.Group>
                    </Col>
                    <Col md={6}>
                        <Form.Group controlId="lecturerName" className="mb-3">
                            <Form.Label>{t('lecturer')}:</Form.Label>
                            <Form.Control
                                type="text"
                                value={classInfo.lecturerName}
                                readOnly
                            />
                        </Form.Group>
                    </Col>
                </Row>
                <Row>
                    <Col md={6}>
                        <Form.Group controlId="courseName" className="mb-3">
                            <Form.Label>{t('course')}:</Form.Label>
                            <Form.Control
                                type="text"
                                value={classInfo.courseName}
                                readOnly
                            />
                        </Form.Group>
                    </Col>
                    <Col md={6}>
                        <Form.Group controlId="academicTerm" className="mb-3">
                            <Form.Label>{t('semester')}:</Form.Label>
                            <Form.Control
                                type="text"
                                value={classInfo.academicTerm}
                                readOnly
                            />
                        </Form.Group>
                    </Col>
                </Row>
            </Form>

            <Form.Group controlId="formFile" className="mb-3">
                <Form.Label>Tải lên bảng điểm từ CSV:</Form.Label>
                <div className="d-flex flex-wrap align-items-center gap-2">
                    <Form.Control
                        type="file"
                        accept=".csv"
                        onChange={handleFileChange}
                        className="flex-grow-1"
                        disabled={classInfo.gradeStatus === "LOCKED"}
                    />
                    <Button variant="success" onClick={uploadCsv} disabled={loading || classInfo.gradeStatus === "LOCKED"}>
                        <FaUpload className="me-2" />
                        Upload CSV
                    </Button>
                </div>
            </Form.Group>

            <fieldset disabled={loading || classInfo.gradeStatus === "LOCKED"}>
                <Table bordered hover>
                    <thead>
                        <tr>
                            <th rowSpan={2}>{t('student-code')}</th>
                            <th rowSpan={2}>{t('full-name')}</th>
                            <th colSpan={displayCount} className="text-center">
                                {t('extra-grades')}{" "}
                                <Button
                                    variant="outline-primary"
                                    size="sm"
                                    onClick={addExtra}
                                    disabled={extraCount >= MAX_EXTRA_GRADES}
                                >
                                    <FaPlus />
                                </Button>
                            </th>
                            <th rowSpan={2}>{t('midterm-grade')}</th>
                            <th rowSpan={2}>{t('final-grade')}</th>
                        </tr>
                        <tr>
                            {Array.from({ length: displayCount }).map((_, idx) => (
                                <th key={idx} className="text-center">
                                    {idx < extraCount && (
                                        <Button
                                            variant="outline-danger"
                                            size="sm"
                                            onClick={() => removeExtraAt(idx)}
                                        >
                                            <FaTimes />
                                        </Button>
                                    )}
                                </th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {students.map((s) => (
                            <tr key={s.studentId}>
                                <td>{s.studentCode}</td>
                                <td>{s.fullName}</td>

                                {Array.from({ length: displayCount }).map((_, idx) => (
                                    <td key={idx}>
                                        <Form.Control
                                            type="number"
                                            value={idx < extraCount ? s.extraGrades[idx] ?? "" : ""}
                                            onChange={(e) =>
                                                idx < extraCount &&
                                                handleChange(s.studentId, "extra", e.target.value, idx)
                                            }
                                            min={0}
                                            max={10}
                                            step={0.1}
                                            disabled={idx >= extraCount}
                                            isInvalid={
                                                !!(
                                                    gradeErrors[s.studentId] &&
                                                    gradeErrors[s.studentId].extra &&
                                                    gradeErrors[s.studentId].extra[idx]
                                                )
                                            }
                                        />
                                        {gradeErrors[s.studentId] &&
                                            gradeErrors[s.studentId].extra &&
                                            gradeErrors[s.studentId].extra[idx] && (
                                                <div className="text-danger small">
                                                    {gradeErrors[s.studentId].extra[idx]}
                                                </div>
                                            )}
                                    </td>
                                ))}

                                <td>
                                    <Form.Control
                                        type="number"
                                        value={s.midtermGrade ?? ""}
                                        onChange={(e) =>
                                            handleChange(s.studentId, "mid", e.target.value)
                                        }
                                        min={0}
                                        max={10}
                                        step={0.1}
                                        isInvalid={
                                            !!(
                                                gradeErrors[s.studentId] &&
                                                gradeErrors[s.studentId].mid
                                            )
                                        }
                                    />
                                    {gradeErrors[s.studentId] && gradeErrors[s.studentId].mid && (
                                        <div className="text-danger small">
                                            {gradeErrors[s.studentId].mid}
                                        </div>
                                    )}
                                </td>
                                <td>
                                    <Form.Control
                                        type="number"
                                        value={s.finalGrade ?? ""}
                                        onChange={(e) =>
                                            handleChange(s.studentId, "final", e.target.value)
                                        }
                                        min={0}
                                        max={10}
                                        step={0.1}
                                        isInvalid={
                                            !!(
                                                gradeErrors[s.studentId] &&
                                                gradeErrors[s.studentId].final
                                            )
                                        }
                                    />
                                    {gradeErrors[s.studentId] && gradeErrors[s.studentId].final && (
                                        <div className="text-danger small">
                                            {gradeErrors[s.studentId].final}
                                        </div>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </fieldset>

            <div className="d-flex justify-content-between m-3">
                <Button
                    className="me-2"
                    variant="primary"
                    onClick={saveGrades}
                    disabled={
                        loading ||
                        classInfo.gradeStatus === "LOCKED" ||
                        hasAnyGradeError()
                    }
                >
                    <FaSave className="me-2" />
                    {capitalizeFirstWord(`${t('save')} ${t('grades-table')}`)}
                </Button>

                <Button variant="danger" onClick={lockGrades} className="ms-2" disabled={loading || classInfo.gradeStatus === "LOCKED"}>
                    <FaLock className="me-2" />
                    {capitalizeFirstWord(`${t('lock')} ${t('grades-table')}`)}
                </Button>
            </div>

        </>
    );
};

export default ClassroomDetail;
