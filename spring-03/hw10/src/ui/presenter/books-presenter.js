import React from 'react'
import {Header} from '../service/header-service.js'
import '../public/less/style.less'
import ReactDOM from "react-dom"
import BookCreatePresenter from './book-create-presenter.js'
import BookUpdatePresenter from './book-update-presenter.js'
import CommentsPresenter from './comments-presenter.js'


export default class BooksPresenter extends React.Component {
    constructor(props) {
        super(props)
        this.state = {books: [], book: '', isFirstLoad: true}
        this.handleDeleteClick = this.handleDeleteClick.bind(this)
        this.handleCreateClick = this.handleCreateClick.bind(this)
        this.handleUpdateClick = this.handleUpdateClick.bind(this)
    }

    async componentDidMount() {
        await this.loadData()
    }

    async loadData() {
        try {
            const response = await fetch('/api/v1/book', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            const books = await response.json()
            this.setState({books})
        } catch (error) {
            console.error("Ошибка получения данных:", error)
        }
    }

    render() {
        const {books} = this.state

        if (!Array.isArray(books)) {
            this.setState({books: []})
            this.handleCreateClick()
        }

        if (books.length === 0 && !this.state.isFirstLoad) {
            this.handleCreateClick()
        }

        if (books.length === 0) {
            this.state.isFirstLoad = false
            return (
                <div>Загрузка...</div>
            )
        }

        return (
            <React.Fragment>
                <Header title={'Books'}/>
                <table className='persons-table'>
                    <thead>
                    <tr className='persons-table'>
                        <th className='persons-table'>ID</th>
                        <th className='persons-table'>Name</th>
                        <th className='persons-table'>Author</th>
                        <th className='persons-table'>Genres</th>
                    </tr>
                    </thead>
                    <tbody>
                    {
                        this.state.books.map((book, i) => (
                            <tr className='persons-table_item' key={i}>
                                <td className='persons-table_item'>{book.id}</td>
                                <td className='persons-table_item'>{book.title}</td>
                                <td className='persons-table_item'>{book.author.fullName}</td>
                                <td className='persons-table_item'>
                                    {book.genres.map((genre, index) => (
                                        <p key={index}>{genre.name}<br/></p>
                                    ))}
                                </td>
                                <td className='persons-table_item'>
                                    <div className='persons-table_button'>
                                        <button type={"button"} onClick={() => this.handleDeleteClick(book.id)}>
                                            Удалить
                                        </button>
                                    </div>
                                </td>
                                <td className='persons-table_item'>
                                    <div className='persons-table_button'>
                                        <button type={"button"} onClick={() => {
                                            this.setState({book: {...book}}, () => this.handleUpdateClick())
                                        }}>
                                            Изменить книгу
                                        </button>
                                    </div>
                                </td>
                                <td className='persons-table_item'>
                                    <div className='persons-table_button'>
                                        <button type={"button"} onClick={() => this.handleCommentsClick(book.id)}>
                                            Посмотреть комментарии
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))
                    }
                    </tbody>
                </table>
                <div className='persons-table_button'>
                    <button type={"button"} onClick={() => this.handleCreateClick()}>
                        Добавить книгу
                    </button>
                </div>
            </React.Fragment>
        )
    }

    async handleDeleteClick(id) {
        try {
            const response = await fetch(`api/v1/book/${id}`, {
                method: 'DELETE'
            })
            if (!response.ok) {
                throw new Error('Ошибка удаления')
            }
            console.log('Книга удалена')
            this.setState(state => ({
                books: state.books.filter(book => book.id !== id)
            }))
        } catch (error) {
            console.error('Ошибка удаления:', error)
        }
    }

    handleCreateClick() {
        ReactDOM.render(<BookCreatePresenter/>, document.getElementById('content'))
        this.state = {books: []}
    }

    handleUpdateClick() {
        ReactDOM.render(<BookUpdatePresenter book={this.state.book}/>, document.getElementById('content'))
        this.state = {books: []}
    }

    handleCommentsClick(id) {
        ReactDOM.render(<CommentsPresenter bookId={id}/>, document.getElementById('content'))
        this.state = {books: []}
    }
}
