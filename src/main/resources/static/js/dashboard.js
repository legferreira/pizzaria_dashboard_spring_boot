const graficos = {};
const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

function parametros() {
    const params = new URLSearchParams();
    for (const [campo, valor] of new FormData(document.getElementById('filtros'))) {
        if (valor) {
            params.append(campo, valor);
        }
    }
    return params.toString();
}

async function buscar(endpoint) {
    const resposta = await fetch(`${endpoint}?${parametros()}`);
    if (!resposta.ok) {
        throw new Error(`erro ${resposta.status} em ${endpoint}`);
    }
    return resposta.json();
}

function formatarData(data) {
    return data ? data.split('-').reverse().join('/') : '-';
}

function mostrarMensagem(texto, tipo) {
    const mensagem = document.getElementById('mensagem');
    mensagem.textContent = texto;
    mensagem.className = texto ? `alert alert-${tipo} mt-3` : 'alert d-none mt-3';
}

function preencherIndicadores(indicadores) {
    for (const [campo, valor] of Object.entries(indicadores)) {
        const emReais = campo === 'faturamento' || campo === 'ticketMedio';
        document.getElementById(campo).textContent = emReais ? moeda.format(valor) : valor;
    }
}

function desenharGrafico(id, tipo, dados, legenda) {
    if (graficos[id]) {
        graficos[id].destroy();
    }
    graficos[id] = new Chart(document.getElementById(id), {
        type: tipo,
        data: {
            labels: Object.keys(dados).map(rotulo => tipo === 'bar' ? formatarData(rotulo) : rotulo),
            datasets: [{ label: legenda, data: Object.values(dados), maxBarThickness: 60 }]
        },
        options: {
            plugins: { legend: { display: tipo !== 'bar' } },
            scales: tipo === 'bar' ? { y: { beginAtZero: true } } : {}
        }
    });
}

function criarLinha(valores) {
    const linha = document.createElement('tr');
    for (const valor of valores) {
        const celula = document.createElement('td');
        celula.textContent = valor ?? '-';
        linha.appendChild(celula);
    }
    return linha;
}

function preencherRanking(sabores) {
    const corpo = document.getElementById('rankingSabores');
    corpo.replaceChildren(...Object.entries(sabores).map(([sabor, total]) => criarLinha([sabor, total])));
}

function preencherTabela(pedidos) {
    const corpo = document.getElementById('tabelaPedidos');
    corpo.replaceChildren(...pedidos.map(p => criarLinha([
        p.cliente, p.descricao, p.sabor, p.status, moeda.format(p.valor), p.atendente,
        formatarData(p.dataPedido), formatarData(p.dataEntrega), p.observacao
    ])));
}

async function carregarDashboard() {
    mostrarMensagem('');
    try {
        const [indicadores, faturamento, status, sabores, pedidos] = await Promise.all([
            buscar('/api/dashboard/indicadores'),
            buscar('/api/dashboard/faturamento-por-periodo'),
            buscar('/api/dashboard/status'),
            buscar('/api/dashboard/sabores'),
            buscar('/api/pedidos')
        ]);
        preencherIndicadores(indicadores);
        desenharGrafico('graficoFaturamento', 'bar', faturamento, 'Faturamento (R$)');
        desenharGrafico('graficoStatus', 'doughnut', status, 'Pedidos');
        preencherRanking(sabores);
        preencherTabela(pedidos);
        document.getElementById('ultimaAtualizacao').textContent = new Date().toLocaleTimeString('pt-BR');
        if (pedidos.length === 0) {
            mostrarMensagem('Nenhum pedido encontrado para os filtros informados.', 'warning');
        }
    } catch (erro) {
        mostrarMensagem(`Não foi possível carregar os dados (${erro.message}). Tente atualizar a página.`, 'danger');
    }
}

document.getElementById('filtros').addEventListener('submit', evento => {
    evento.preventDefault();
    carregarDashboard();
});

document.getElementById('limpar').addEventListener('click', () => {
    document.getElementById('filtros').reset();
    carregarDashboard();
});

document.getElementById('atualizar').addEventListener('click', carregarDashboard);

carregarDashboard();
setInterval(carregarDashboard, 60000);
