import { Alert, Container } from "react-bootstrap";
import SemesterTable from "./SemesterTable";
import { useEffect, useState } from "react";
import { authApis, endpoints } from "../configs/Apis";
import MySpinner from "./layouts/MySpinner";
import { useSearchParams } from "react-router-dom";

const Grade = () => {
    const [gradesBySemester, setGradesBySemester] = useState([]);
    const [loading, setLoading] = useState(false);
    const [q] = useSearchParams();

    const loadGrades = async () => {
        try {
            setLoading(true)

            let url = `${endpoints['student-grades']}`;

            const kw = q.get('kw');
            if (kw) {
                url = `${url}?kw=${kw}`;
            }

            const res = await authApis().get(url);
            const data = res.data

            const grouped = data.reduce((idx, grade) => {
                const semesterKey = `${grade.gradeDetail.semester.academicYear.year} - ${grade.gradeDetail.semester.semesterType}`;
                if (!idx[semesterKey]) {
                    idx[semesterKey] = {
                        semesterTitle: `Học kỳ ${grade.gradeDetail.semester.semesterType === "FIRST_TERM"
                            ? "1" : grade.gradeDetail.semester.semesterType === "SECOND_TERM" ? "2" : "3"} 
                        - Năm học ${grade.gradeDetail.semester.academicYear.year}`,
                        subjects: [],
                    };
                }
                idx[semesterKey].subjects.push({
                    code: grade.gradeDetail.course.id,
                    classCode: grade.classroomName,
                    name: grade.gradeDetail.course.name,
                    credit: grade.gradeDetail.course.credit,
                    extraGrade: grade.gradeDetail.extraGradeSet.map(i => i.grade),
                    midTermGrade: grade.gradeDetail.midtermGrade,
                    finalGrade: grade.gradeDetail.finalGrade
                });
                return idx;
            }, {});

            const groupedArray = Object.values(grouped);
            setGradesBySemester(groupedArray);

        } catch (ex) {
            console.error(ex)
        } finally {
            setLoading(false)
        }
    };

    useEffect(() => {
        loadGrades();
    }, [q]);

    return (
        <Container className="p-3" style={{ minHeight: "100vh" }}>
            {gradesBySemester.length > 0 ? gradesBySemester.map((semester, idx) =>
                <SemesterTable
                    key={idx}
                    semesterTitle={semester.semesterTitle}
                    subjects={semester.subjects}
                    summary={null}
                />
            ) : <Alert variant="info" className="m-2">
                Không có điểm!
            </Alert>
            }

            {loading && <MySpinner />}
        </Container >
    );
};

export default Grade;