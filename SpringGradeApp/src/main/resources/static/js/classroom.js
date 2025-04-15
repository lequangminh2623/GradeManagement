/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/javascript.js to edit this template
 */

new TomSelect('#lecturerSelect', {
    placeholder: 'Tìm giảng viên...',
    allowEmptyOption: false
});

new TomSelect('#courseSelect', {
    placeholder: 'Tìm môn học...',
    allowEmptyOption: false
});

new TomSelect('#semesterSelect', {
    placeholder: 'Tìm học kỳ...',
    allowEmptyOption: false
});

// Chọn nhiều (Sinh viên)
tomSelect = new TomSelect('#studentSelect', {
    plugins: ['remove_button'],
    placeholder: 'Tìm và chọn sinh viên...'
});

function deleteClassroom(url, id) {
    if (confirm("Bạn chắc chắn muốn xóa?")) {
        fetch(`${url}${id}`, {
            method: 'DELETE'
        }).then(res => {
            if (res.status === 204) {
                alert("Xóa thành công!");
                location.reload();
            } else {
                return res.text().then(text => {
                    alert(text);
                });
            }
        });
    }
}




function removeStudentFromClassroom(endpoint, classroomId, studentId) {
    if (confirm("Bạn có chắc chắn muốn xóa sinh viên và điểm khỏi lớp học này không?")) {
        fetch(endpoint + `${classroomId}/students/${studentId}`, {
            method: "DELETE"
        }).then(res => {
            if (res.status === 204) {
                location.reload();
            } else {
                alert("Có lỗi xảy ra");
            }
        });
    }
}

let maxExtra = 3;

function addExtraColumn() {
    const headerRow = document.querySelector("#extra-header-buttons");
    const anyCell = document.querySelector(".extra-grade-cells");

    if (!anyCell)
        return;

    const existingIndices = Array.from(anyCell.children)
            .map(input => parseInt(input.name.match(/\[(\d+)\]$/)?.[1]))
            .filter(n => !isNaN(n));

    let newIndex = 0;
    while (existingIndices.includes(newIndex)) {
        newIndex++;
    }

    if (newIndex >= maxExtra)
        return;

    document.querySelectorAll(".extra-grade-cells").forEach((cell) => {
        const studentId = cell.getAttribute("data-student-id");

        const input = document.createElement("input");
        input.type = "number";
        input.className = "form-control form-control-sm text-center";
        input.name = `extraPoints[${studentId}][${newIndex}]`;
        input.min = 0;
        input.max = 10;
        input.step = 0.1;
        input.style.width = "60px";

        cell.appendChild(input);
    });

    const removeButton = document.createElement("button");
    removeButton.type = "button";
    removeButton.className = "btn btn-outline-danger btn-sm";
    removeButton.innerText = "❌";
    removeButton.setAttribute("onclick", `removeExtraColumn(${newIndex})`);

    headerRow.appendChild(removeButton);
}

function removeExtraColumn(index) {
    if (confirm(`Bạn có chắc muốn xoá cột điểm bổ sung ${index + 1}?`)) {
        document.querySelectorAll(".extra-grade-cells").forEach(cell => {
            const inputs = cell.querySelectorAll("input");
            if (inputs.length > index) {
                inputs[index].remove();
            }
        });

        const headerButtons = document.querySelector("#extra-header-buttons");
        if (headerButtons && headerButtons.children.length > index) {
            headerButtons.children[index].remove();
        }
    }
}

document.addEventListener("DOMContentLoaded", () => {
    const select = document.getElementById("studentSelect");
    const tbody = document.getElementById("student-table-body");
    const addBtn = document.getElementById("addStudent");

    const getExtraColumnCount = () => {
        const cell = document.querySelector(".extra-grade-cells");
        return cell ? cell.querySelectorAll("input").length : 0;
    };

    addBtn.addEventListener("click", (e) => {
        e.preventDefault();

        const selectedOptions = Array.from(select.selectedOptions);
        const extraCount = getExtraColumnCount();

        selectedOptions.forEach(option => {
            const studentId = option.value;
            const [code, name] = option.text.split(" - ");
            const rowId = `row-student-${studentId}`;

            if (document.getElementById(rowId))
                return;

            const tr = document.createElement("tr");
            tr.id = rowId;

            tr.innerHTML = `
                <td class="text-center align-middle">${code}</td>
                <td class="align-middle">${name}</td>
                <td class="align-middle">
                    <div class="extra-grade-cells d-flex gap-2 justify-content-center" data-student-id="${studentId}">
                        ${Array(extraCount).fill().map((_, i) => `
                            <input type="number" class="form-control form-control-sm text-center"
                                name="extraPoints[${studentId}][${i}]" min="0" max="10" step="0.1"
                                style="width: 60px;" />
                        `).join('')}
                    </div>
                </td>
                <td class="text-center align-middle">
                    <input type="number" class="form-control form-control-sm text-center"
                        name="midtermGrade[${studentId}]" min="0" max="10" step="0.1" style="width: 60px;" />
                </td>
                <td class="text-center align-middle">
                    <input type="number" class="form-control form-control-sm text-center"
                        name="finalGrade[${studentId}]" min="0" max="10" step="0.1" style="width: 60px;" />
                </td>
                <td class="text-center align-middle">
                    <button type="button" class="btn btn-danger btn-sm">🗑</button>
                </td>
            `;

            // Xử lý nút xóa
            tr.querySelector("button").addEventListener("click", () => {
                if (confirm("Bạn có chắc chắn muốn xóa sinh viên và điểm khỏi lớp học này không?")) {
                    tomSelect.removeItem(studentId);
                    tr.remove();
                }
            });

            tbody.appendChild(tr);
            tomSelect.addItem(studentId, true);
        });

    });
});
