import React, { useContext, useEffect, useState } from "react";
import { Container, Form, Button, Table } from "react-bootstrap";
import { Chart, registerables } from "chart.js"; // Import registerables
import { authApis, endpoints } from "../configs/Apis";
import { MyUserContext } from "../configs/MyContexts";

// Register all Chart.js components
Chart.register(...registerables);

let chartInstance = null; // Keep track of the chart instance

const Home = () => {
    const [semesters, setSemesters] = useState([]);
    const [semesterId, setSemesterId] = useState("1");
    const [result, setResult] = useState(null);
    const user = useContext(MyUserContext);

    useEffect(() => {
        if (user.role === "ROLE_LECTURER") {
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
        }
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
            {user.role === "ROLE_LECTURER" ? (
                <>
                    <Form onSubmit={handleSubmit}>
                        <div className="col-md-6">
                            <Form.Group controlId="semesterSelect">
                                <Form.Label>Học kỳ:</Form.Label>
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
                                Xem thống kê
                            </Button>
                        </div>
                    </Form>

                    {result && (
                        <>
                            <h2 className="mt-5">Biểu đồ thống kê tỷ lệ sinh viên yếu theo môn học</h2>
                            <canvas id="weakCourseChart" width="600" height="300"></canvas>

                            <h3 className="mt-5">Danh sách môn học có tỷ lệ yếu cao (&ge;40%)</h3>
                            <ul>
                                {result.criticalCourses.map((course, index) => (
                                    <li key={index}>{course}</li>
                                ))}
                            </ul>

                            <h3 className="mt-5">Danh sách sinh viên yếu</h3>
                            <Table bordered>
                                <thead>
                                    <tr>
                                        <th>Mã SV</th>
                                        <th>Họ tên</th>
                                        <th>Môn học</th>
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
                </>
            ) : (
                <h1 className="text-center mt-5">Chào mừng đến với Grade Management System!</h1>
            )}
        </Container>
    );
};

export default Home;