import React from 'react'
import {Header} from '../service/header-service.js'
import ReactDOM from 'react-dom'
import App from './books-presenter.js'


export default class BookCreatePresenter extends React.Component {
    constructor(props) {
        super(props)
        this.state = {authors: [], genres: [], title: '', authorId: '', genreIds: []}
        this.handleTitleChange = this.handleTitleChange.bind(this)
        this.handleAuthorChange = this.handleAuthorChange.bind(this)
        this.handleGenreChange = this.handleGenreChange.bind(this)
        this.handleSubmit = this.handleSubmit.bind(this)
        this.handleBooksClick = this.handleBooksClick.bind(this)
    }

    async componentDidMount() {
        await this.fetchAuthors()
        await this.fetchGenres()
    }

    async fetchAuthors() {
        try {
            const response = await fetch('/api/v1/author', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            if (!response.ok) {
                throw new Error('Ошибка получения авторов')
            }
            const authors = await response.json()
            this.setState({authors})
        } catch (error) {
            console.error('Ошибка получения авторов:', error)
        }
    }

    async fetchGenres() {
        try {
            const response = await fetch('/api/v1/genre', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            if (!response.ok) {
                throw new Error('Ошибка получения жанров')
            }
            const genres = await response.json()
            this.setState({genres})
        } catch (error) {
            console.error('Ошибка получения жанров:', error)
        }
    }

    handleTitleChange(event) {
        this.setState({title: event.target.value})
    }

    handleAuthorChange(event) {
        this.setState({authorId: event.target.value})
    }

    handleGenreChange(event) {
        const selectedGenreIds = Array.from(event.target.selectedOptions)
            .map(option => option.value)
        this.setState({genreIds: selectedGenreIds})
    }

    async handleSubmit(event) {
        event.preventDefault()
        const {title, authorId, genreIds} = this.state
        const data = {title, authorId, genreIds}

        try {
            const response = await fetch('/api/v1/book', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })

            if (!response.ok) {
                throw new Error('Ошибка создания книги')
            }

            console.log('Книга создана успешно')
            this.handleBooksClick()
        } catch (error) {
            console.error('Ошибка создания книги:', error)
        }
    }

    render() {
        const {authors, genres, title, authorId, genreIds} = this.state

        if (authors.length === 0 || genres.length === 0) {
            return (
                <div>Загрузка...</div>
            )
        }

        return (
            <React.Fragment>
                <Header title={'Create book'}/>
                <form onSubmit={this.handleSubmit}>
                    <table className='persons-table'>
                        <thead>
                        <tr className='persons-table'>
                            <th className='persons-table'>Название:</th>
                            <th className='persons-table'>Автор:</th>
                            <th className='persons-table'>Жанры:</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr className='persons-table_item'>
                            <td className='persons-table_item'>
                                <label>
                                    <input className='persons-table_item__title' type="text" value={title}
                                           onChange={this.handleTitleChange}
                                           required/>
                                </label>
                            </td>
                            <td className='persons-table_item'>
                                <label>
                                    <select value={authorId} onChange={this.handleAuthorChange} required>
                                        <option value="">Выберите автора</option>
                                        {authors.map(author => (
                                            <option key={author.id} value={author.id}>
                                                {author.fullName}
                                            </option>
                                        ))}
                                    </select>
                                </label>
                            </td>
                            <td className='persons-table_item'>
                                <label>
                                    <select multiple value={genreIds} onChange={this.handleGenreChange}
                                            required>
                                        {genres.map(genre => (
                                            <option key={genre.id} value={genre.id}>
                                                {genre.name}
                                            </option>
                                        ))}
                                    </select>
                                </label>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                    <table>
                        <tbody>
                        <tr>
                            <td>
                                <div className='persons-table_button'>
                                    <button type="submit">Создать книгу</button>
                                </div>
                            </td>
                            <td>
                                <div className='persons-table_button'>
                                    <button type="button" onClick={() => this.handleBooksClick()}>Отмена</button>
                                </div>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </form>
            </React.Fragment>
        )
    }

    handleBooksClick() {
        ReactDOM.render(<App/>, document.getElementById('content'))
        this.state = {authors: [], genres: [], title: '', authorId: '', genreIds: []}
    }
}
