import { Alert, Container } from "react-bootstrap";
import SemesterTable from "./SemesterTable";
import { useEffect, useState } from "react";
import { authApis, endpoints } from "../../configs/Apis";
import MySpinner from "../layouts/MySpinner";
import { useSearchParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { capitalizeFirstWord } from "../../utils/utils"

const Grade = () => {
    const [gradesBySemester, setGradesBySemester] = useState([]);
    const [loading, setLoading] = useState(false);
    const [q] = useSearchParams();
    const { i18n, t } = useTranslation()

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
                        semesterTitle: i18n.language === "vi" ?
                            `${t('semester')} ${t(`semesterTypes.${grade.gradeDetail.semester.semesterType}`)}
                        - ${t('year')} ${grade.gradeDetail.semester.academicYear.year}` :
                            `${t(`semesterTypes.${grade.gradeDetail.semester.semesterType}`)}
                        - ${grade.gradeDetail.semester.academicYear.year}`,
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
    }, [q, i18n.language]);

    return (
        <Container className="p-3" style={{ minHeight: "100vh" }}>
            <h3 className="mb-3">{t('grades-table')}</h3>
            {gradesBySemester.length > 0 ? gradesBySemester.map((semester, idx) =>
                <SemesterTable
                    key={idx}
                    semesterTitle={semester.semesterTitle}
                    subjects={semester.subjects}
                    summary={null}
                />
            ) : <Alert variant="info" className="m-2">
                {capitalizeFirstWord(`${t('no')} ${t('grades')}`)}
            </Alert>
            }

            {loading && <MySpinner />}
        </Container >
    );
};

export default Grade;