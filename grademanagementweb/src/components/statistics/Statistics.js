import React, { useEffect, useState } from "react";
import { Container, Form, Button, Table } from "react-bootstrap";
import { Chart, registerables } from "chart.js"; // Import registerables
import { authApis, endpoints } from "../../configs/Apis";
import { useTranslation } from "react-i18next";
Chart.register(...registerables);
let chartInstance = null;

const Statistics = () => {
    const [semesters, setSemesters] = useState([]);
    const [semesterId, setSemesterId] = useState("1");
    const [result, setResult] = useState(null);
    const { t } = useTranslation()

    useEffect(() => {
        // Fetch initial data (semesters and analysis for the default semester)
        const fetchInitialData = async () => {
            try {
                const res = await authApis().get(endpoints["analysis"](semesterId));
                const { analysisResult, semesters } = res.data;

                setSemesters(semesters);
                if (semesters.length > 0 && !semesterId) {
                    setSemesterId(semesters[0].id); // Default to the first semester
                }
                setResult(analysisResult);

                // Render the chart after the result is set
                renderChart(analysisResult.courseWeakRatios);
            } catch (err) {
                console.error("Error fetching initial data:", err);
            }
        };

        fetchInitialData();

    }, []);

    const fetchAnalysis = async () => {
        try {
            const res = await authApis().get(endpoints["analysis"](semesterId));
            const { analysisResult } = res.data;
            setResult(analysisResult);

            // Render the chart after the result is set
            renderChart(analysisResult.courseWeakRatios);
        } catch (err) {
            console.error("Error fetching analysis:", err);
        }
    };

    const renderChart = (courseWeakRatios) => {
        // Wait for the DOM to update before accessing the canvas element
        setTimeout(() => {
            const canvas = document.getElementById("weakCourseChart");
            if (!canvas) {
                console.error("Canvas element not found. Skipping chart rendering.");
                return;
            }

            const ctx = canvas.getContext("2d");

            // Destroy the existing chart instance if it exists
            if (chartInstance) {
                chartInstance.destroy();
            }

            const labels = Object.keys(courseWeakRatios);
            const data = Object.values(courseWeakRatios);

            // Create a new chart instance and store it
            chartInstance = new Chart(ctx, {
                type: "bar",
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: "% Sinh viên yếu",
                            data: data,
                            backgroundColor: "rgba(255, 99, 132, 0.6)",
                            borderColor: "rgba(255, 99, 132, 1)",
                            borderWidth: 1,
                        },
                    ],
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: true,
                            max: 100,
                        },
                    },
                    plugins: {
                        legend: {
                            display: false,
                        },
                    },
                },
            });
        }, 500); // Delay execution to ensure the DOM is updated
    };

    const handleSemesterChange = (e) => {
        setSemesterId(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        fetchAnalysis();
    };

    return (
        <Container className="p-3" style={{ minHeight: "100vh" }}>
            <Form onSubmit={handleSubmit}>
                <h2 className="mb-3">{t('statistics')}</h2>

                <div className="col-md-6">
                    <Form.Group controlId="semesterSelect">
                        <Form.Label>{t('semester')}:</Form.Label>
                        <Form.Select value={semesterId} onChange={handleSemesterChange}>
                            {semesters.map((s) => (
                                <option key={s.id} value={s.id} selected={s.id === semesterId}>
                                    {`${s.academicYear.year} - ${s.semesterType}`}
                                </option>
                            ))}
                        </Form.Select>
                    </Form.Group>
                </div>
                <div className="mt-3">
                    <Button type="submit" className="btn btn-primary">
                        {t('view-statistics')}
                    </Button>
                </div>
            </Form>

            {result && (
                <>
                    <h3 className="mt-4">{t('chart-title')}</h3>
                    <canvas id="weakCourseChart" height="100"></canvas>

                    <h3 className="mt-4">{t('list-courses')} (&ge;40%)</h3>
                    <ul>
                        {result.criticalCourses.map((course, index) => (
                            <li key={index}>{course}</li>
                        ))}
                    </ul>

                    <h3 className="mt-5">{t('list-students')}</h3>
                    <Table bordered hover responsive className="mt-3">
                        <thead>
                            <tr>
                                <th>{t('student-code')}</th>
                                <th>{t('full-name')}</th>
                                <th>{t('course')}</th>
                            </tr>
                        </thead>
                        <tbody>
                            {result.weakStudentList.map((student) => (
                                <tr key={`${student.studentId}-${student.courseName}`}>
                                    <td>{student.studentCode}</td>
                                    <td>{student.fullName}</td>
                                    <td>{student.courseName}</td>
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                </>
            )}
        </Container>
    )
}

export default Statistics;