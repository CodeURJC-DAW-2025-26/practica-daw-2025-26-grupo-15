const ctx = document.getElementById('comparisonChart');
const followers = ctx?.dataset?.numFollowers ?? 0;
const following = ctx?.dataset?.numFollowing ?? 0;

document.addEventListener("DOMContentLoaded", () => {

    if (!ctx) return;

    const maxValue = Math.max(followers, following);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: ['Followers', 'Following'],
            datasets: [{
                label: 'Comparison',
                data: [followers, following],
                backgroundColor: [
                    '#b8cdf2',
                    '#b6f2e1'
                ],
                borderColor: [
                    '#b8cdf2',
                    '#b6f2e1'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false, // permite tamaño fijo del contenedor
            scales: {
                y: {
                    beginAtZero: true,
                    suggestedMax: maxValue * 1.2, // escala dinámica con margen
                    ticks: {
                        precision: 0 // solo enteros
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });

});
