document.addEventListener("DOMContentLoaded", function () {
	var currentPath = window.location.pathname;
	if (currentPath.split('/').length > 1) {
		currentPath = '/' + currentPath.split('/')[1];
	}
	var navItems = document.querySelectorAll("#home-nav li");

	navItems.forEach(function (item) {
		var link = item.querySelector("a");
		if (link) {
			var href = link.getAttribute("href");
			if (currentPath === href) {
				item.classList.add("active");
			}
		}
	});

	var flashElement = document.getElementById('flashMessage');

	if (flashElement) {
		// Apply the animation class
		flashElement.classList.add("animate--drop-in-fade-out");

		// Remove the animation class after 3 seconds (adjust as needed)
		setTimeout(function() {
			flashElement.classList.remove("animate--drop-in-fade-out");
		}, 3000);
	}


	function calculateSemester() {
		const currentDate = new Date();
		const currentMonth = currentDate.getMonth() + 1;

		let semester, academicYear;

		if (currentMonth >= 1 && currentMonth <= 5) {
		  // 2nd semester
		  semester = "2nd semester";
		  academicYear = `${currentDate.getFullYear() - 1}-${currentDate.getFullYear()}`;
		} else if (currentMonth >= 9 && currentMonth <= 12) {
		  // 1st semester
		  semester = "1st semester";
		  academicYear = `${currentDate.getFullYear()}-${currentDate.getFullYear() + 1}`;
		} else {
		  // 3rd semester
		  semester = "3rd semester";
		  academicYear = `${currentDate.getFullYear() - 1}-${currentDate.getFullYear()}`;
		}

		return `${semester}/${academicYear}`;
	}

	const semester = document.getElementById("semester");
	if (semester) {
		const result = calculateSemester();
		semester.innerText = result;
	}


});

function confirmDelete() {
    var confirmDelete = confirm("Are you sure you want to delete this?");
    return confirmDelete;
}

function confirmBanned() {
	var confirmBanned = confirm("Are you sure you want to banned this person?");
	return confirmBanned;
}

function confirmUnBanned() {
	var confirmUnBanned = confirm("Are you sure you want to unbanned this person?");
	return confirmUnBanned;
}

function confirmUpdate() {
    var confirmDelete = confirm("Are you sure you want to update this?");
    return confirmDelete;
}

const renderTab = (event, obj) => {
    event.preventDefault();

    // Remove the 'active' class from all tabs
    const allTabs = document.querySelectorAll('.nav-tabs li');
    allTabs.forEach(tab => tab.classList.remove('active'));

    // Add the 'active' class to the clicked tab
    obj.parentElement.classList.add('active');

    // Remove the 'active' class from all tab panes
    const allTabPanes = document.querySelectorAll('.tab-content .tab-pane');
    allTabPanes.forEach(pane => pane.classList.remove('active'));

    // Add the 'active' class to the corresponding tab pane
    document.getElementById(obj.getAttribute('href').substring(1)).classList.add('active');
}

