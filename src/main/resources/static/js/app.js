window.DiscDrop = (function () {
    const THEMES = ['dark', 'light'];
    const STORAGE_KEY = 'discdrop-theme';

    function init() {
        const saved = localStorage.getItem(STORAGE_KEY) || 'dark';
        applyTheme(saved);
    }

    function applyTheme(theme) {
        if (!THEMES.includes(theme)) {
            theme = 'dark';
        }
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem(STORAGE_KEY, theme);
        const moon = document.getElementById('theme-icon-moon');
        const sun = document.getElementById('theme-icon-sun');
        if (moon && sun) {
            moon.classList.toggle('hidden', theme === 'light');
            sun.classList.toggle('hidden', theme === 'dark');
        }
    }

    function toggleTheme() {
        const current = document.documentElement.getAttribute('data-theme') || 'dark';
        applyTheme(current === 'dark' ? 'light' : 'dark');
    }

    function clearSearch() {
        const dropdown = document.getElementById('search-dropdown');
        const input = document.getElementById('search-input');
        const form = document.getElementById('search-form');
        if (dropdown) {
            dropdown.innerHTML = '';
        }
        if (input) {
            input.value = '';
        }
        if (form) {
            form.reset();
        }
    }

    function refreshFeed() {
        if (typeof htmx !== 'undefined' && document.getElementById('feed-list')) {
            htmx.ajax('GET', '/feed?offset=0', { target: '#feed-list', swap: 'innerHTML' });
        }
    }

    function closeOnClickOutside(event) {
        const searchWrap = document.getElementById('search-wrap');
        if (searchWrap && !searchWrap.contains(event.target)) {
            const dropdown = document.getElementById('search-dropdown');
            if (dropdown) {
                dropdown.innerHTML = '';
            }
        }
        const settingsDropdown = document.getElementById('settings-dropdown');
        if (settingsDropdown && !settingsDropdown.contains(event.target)) {
            settingsDropdown.classList.remove('dropdown-open');
        }
    }

    document.addEventListener('click', closeOnClickOutside);
    document.addEventListener('DOMContentLoaded', init);

    return {
        toggleTheme: toggleTheme,
        clearSearch: clearSearch,
        refreshFeed: refreshFeed
    };
})();
